package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Relationship;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@ToString
public class ComponentSerializationService {

    private static final int PARALLEL_SORT_THRESHOLD = 1_000;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

    private static final String COMPONENTS_DIR = "discovered-components";
    private static final String SNAPSHOT_FILE_PREFIX = "components-snapshot-";
    private static final String LATEST_SNAPSHOT_FILE = "components-latest.json";

    /**
     * Data structure representing a serialized component snapshot.
     */
    public static class ComponentSnapshot {
        public String timestamp;
        public String generatedBy;
        public String version;
        public Map<String, ContainerSnapshot> containers;

        public ComponentSnapshot() {
            this.containers = new LinkedHashMap<>();
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            this.generatedBy = "C4ModelGenerator";
            this.version = "1.0";
        }
    }

    /**
     * Data structure representing components within a container.
     */
    public static class ContainerSnapshot {
        public String containerName;
        public String containerDescription;
        public String containerTechnology;
        public int componentCount;
        public Map<String, SerializedComponent> components;

        public ContainerSnapshot() {
            this.components = new LinkedHashMap<>();
        }
    }

    /**
     * Data structure representing a single serialized component.
     */
    public static class SerializedComponent {
        public String name;
        public String description;
        public String technology;
        public Set<String> tags;
        public String type;
        public List<SerializedRelationship> relationships;
        public Map<String, String> metadata;

        public SerializedComponent() {
            this.tags = new TreeSet<>();
            this.relationships = new ArrayList<>();
            this.metadata = new LinkedHashMap<>();
        }

    }

    /**
     * Data structure representing a component relationship.
     */
    public static class SerializedRelationship {
        public String target;
        public String description;
        public String type;
        public Map<String, String> properties;

        public SerializedRelationship() {
            this.properties = new LinkedHashMap<>();
        }
    }

    /**
     * Data structure for component comparison results.
     */
    public static class ComponentComparisonResult {
        public final Set<String> newComponents;
        public final Set<String> removedComponents;
        public final Set<String> modifiedComponents;
        public final boolean hasChanges;

        public ComponentComparisonResult(Set<String> newComponents,
                                         Set<String> removedComponents,
                                         Set<String> modifiedComponents) {
            // Coalesce nulls to empty sets
            this.newComponents      = newComponents      != null ? newComponents      : Collections.emptySet();
            this.removedComponents  = removedComponents  != null ? removedComponents  : Collections.emptySet();
            this.modifiedComponents = modifiedComponents != null ? modifiedComponents : Collections.emptySet();

            this.hasChanges =
                    !this.newComponents.isEmpty() ||
                            !this.removedComponents.isEmpty() ||
                            !this.modifiedComponents.isEmpty();
        }

        /** Factory for the “no changes” result */
        public static ComponentComparisonResult noChanges() {
            return new ComponentComparisonResult(
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptySet()
            );
        }
    }

    /**
     * Serializes all components from the provided containers to JSON format.
     *
     * @param containers Map of container names to Container objects
     * @return ComponentSnapshot containing all serialized component data
     */
    public static ComponentSnapshot serializeComponents(Map<String, Container> containers) {
        ComponentSnapshot snapshot = new ComponentSnapshot();

        for (Map.Entry<String, Container> entry : containers.entrySet()) {
            String containerName = entry.getKey();
            Container container = entry.getValue();

            ContainerSnapshot containerSnapshot = new ContainerSnapshot();
            containerSnapshot.containerName = container.getName();
            containerSnapshot.containerDescription = container.getDescription();
            containerSnapshot.containerTechnology = container.getTechnology();
            containerSnapshot.componentCount = container.getComponents().size();

            // Serialize each component in the container
            for (Component component : container.getComponents()) {
                SerializedComponent serializedComponent = serializeComponent(component);
                containerSnapshot.components.put(component.getName(), serializedComponent);
            }

            snapshot.containers.put(containerName, containerSnapshot);

        }

        return snapshot;
    }

    /**
     * Serializes a single component to the SerializedComponent format.
     *
     * @param component The Structurizr Component to serialize
     * @return SerializedComponent representation
     */
    private static SerializedComponent serializeComponent(Component component) {
        SerializedComponent serialized = new SerializedComponent();

        serialized.name = component.getName();
        serialized.description = component.getDescription();
        serialized.technology = component.getTechnology();
        serialized.type = "Component"; // Default type since Component doesn't have getType() method

        // Add tags
        if (component.getTags() != null) {
            serialized.tags.addAll(Arrays.asList(component.getTags().split(",")));
        }

        // Add relationships
        for (Relationship relationship : component.getRelationships()) {
            SerializedRelationship serializedRel = new SerializedRelationship();
            serializedRel.target = relationship.getDestination().getName();
            serializedRel.description = relationship.getDescription();
            serializedRel.type = "uses"; // Default type

            // Add relationship properties if any
            if (relationship.getProperties() != null) {
                serializedRel.properties.putAll(relationship.getProperties());
            }

            serialized.relationships.add(serializedRel);
        }

        // Add component metadata
        if (component.getProperties() != null) {
            serialized.metadata.putAll(component.getProperties());
        }

        return serialized;
    }

    /**
     * Saves a component snapshot to a JSON file.
     *
     * @param snapshot   The ComponentSnapshot to save
     * @param outputPath The path where to save the snapshot file
     * @throws IOException if file writing fails
     */
    public static void saveSnapshot(ComponentSnapshot snapshot, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        outputFile.getParentFile().mkdirs(); // Create directories if they don't exist

        objectMapper.writeValue(outputFile, snapshot);
        System.out.println(" Component snapshot saved to: " + outputFile.getAbsolutePath());
    }

    /**
     * Saves the current snapshot as the latest snapshot and creates a timestamped backup.
     *
     * @param snapshot The ComponentSnapshot to save
     * @throws IOException if file writing fails
     */
    public static void saveSnapshotWithHistory(ComponentSnapshot snapshot) throws IOException {
        // Create components directory if it doesn't exist
        File componentsDir = new File(COMPONENTS_DIR);
        componentsDir.mkdirs();

        // Save timestamped snapshot
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String timestampedFile = COMPONENTS_DIR + "/" + SNAPSHOT_FILE_PREFIX + timestamp + ".json";
        saveSnapshot(snapshot, timestampedFile);

        // Save as latest snapshot
        String latestFile = COMPONENTS_DIR + "/" + LATEST_SNAPSHOT_FILE;
        saveSnapshot(snapshot, latestFile);

        System.out.println(" Component snapshots saved:");
        System.out.println("  - Latest: " + latestFile);
        System.out.println("  - Timestamped: " + timestampedFile);
    }

    /**
     * Loads a component snapshot from a JSON file.
     *
     * @param filePath Path to the snapshot file
     * @return ComponentSnapshot object, or null if file doesn't exist or can't be read
     */
    public static ComponentSnapshot loadSnapshot(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("Snapshot file not found: " + filePath);
                return null;
            }

            ComponentSnapshot snapshot = objectMapper.readValue(file, ComponentSnapshot.class);
            System.out.println(" Component snapshot loaded from: " + filePath);
            return snapshot;
        } catch (IOException e) {
            System.out.println(" Failed to load snapshot from " + filePath + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads the latest component snapshot.
     *
     * @return ComponentSnapshot object, or null if no snapshot exists
     */
    public static ComponentSnapshot loadLatestSnapshot() {
        String latestFile = COMPONENTS_DIR + "/" + LATEST_SNAPSHOT_FILE;
        return loadSnapshot(latestFile);
    }

    public static boolean snapshotsAreEqual(ComponentSnapshot oldSnap,
                                            ComponentSnapshot newSnap) throws Exception {
        String oldHash = oldSnap == null
                ? ""
                : snapshotContentHash(oldSnap);
        String newHash = snapshotContentHash(newSnap);

        System.out.println("Old content hash: " + oldHash);
        System.out.println("New content hash: " + newHash);

        boolean same = oldHash.equals(newHash);
        System.out.println("compare hash: " + same);

        if (same) {

            return true;
        } else {

            return false;
        }
    }

    private static void sortRecursively(JsonNode node) {
        if (node.isObject()) {
            // Recurse into each field value
            node.fields().forEachRemaining(e -> sortRecursively(e.getValue()));
        }
        else if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            int size = array.size();

            // For tiny arrays, just recurse and return
            if (size <= 1) {
                array.forEach(ComponentSerializationService::sortRecursively);
                return;
            }

            // 1) Copy to a fixed array (pre‑sized)
            JsonNode[] elems = new JsonNode[size];
            int idx = 0;
            for (JsonNode e : array) {
                elems[idx++] = e;
            }

            // 2) Single pass to detect element type
            boolean allTextual    = true;
            boolean allHaveTarget = true;
            for (JsonNode e : elems) {
                if (!e.isTextual())                           allTextual    = false;
                if (!(e.isObject() && e.has("target")))       allHaveTarget = false;
                if (!allTextual && !allHaveTarget) break;
            }

            // 3) Pick comparator
            Comparator<JsonNode> cmp;
            if (allTextual) {
                cmp = Comparator.comparing(JsonNode::asText);
            } else if (allHaveTarget) {
                cmp = Comparator.comparing(e -> e.get("target").asText());
            } else {
                cmp = Comparator.comparing(JsonNode::toString);
            }

            // 4) Sort (parallel for big arrays)
            if (size >= PARALLEL_SORT_THRESHOLD) {
                Arrays.parallelSort(elems, cmp);
            } else {
                Arrays.sort(elems, cmp);
            }

            // 5) Overwrite in‑place
            for (int i = 0; i < size; i++) {
                array.set(i, elems[i]);
            }

            // 6) Recurse into sorted elements
            for (JsonNode e : elems) {
                sortRecursively(e);
            }
        }
        // else: primitive (text, number, boolean)—nothing to do
    }



    public static String snapshotContentHash(ComponentSnapshot snap) throws Exception {

         final ObjectMapper CANONICAL_MAPPER = JsonMapper.builder()
                 .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                 .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                 .build();
        ObjectNode root = CANONICAL_MAPPER.valueToTree(snap);

        // 2) Remove volatile fields
        root.remove("timestamp");
        root.remove("generatedBy");
        root.remove("version");

        // 3) Recursively sort everything
        sortRecursively(root);

        // 4) Serialize and hash
        String canon = CANONICAL_MAPPER.writeValueAsString(root);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(canon.getBytes(StandardCharsets.UTF_8));

        // Hex‑encode
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    /**
     * Compares two component snapshots to identify changes.
     *
     * @param oldSnapshot Previous snapshot (can be null)
     * @param newSnapshot Current snapshot
     * @return ComponentComparisonResult containing the differences
     */
    public static ComponentComparisonResult compareSnapshots(ComponentSnapshot oldSnapshot, ComponentSnapshot newSnapshot) {
        Set<String> newComponents = new TreeSet<>();
        Set<String> removedComponents = new TreeSet<>();
        Set<String> modifiedComponents = new TreeSet<>();

        if (oldSnapshot == null) {
            // All components are new
            for (ContainerSnapshot container : newSnapshot.containers.values()) {
                for (String componentName : container.components.keySet()) {
                    newComponents.add(container.containerName + "::" + componentName);
                }
            }
        } else {
            // Compare containers and components
            Map<String, SerializedComponent> oldComponentsFlat = flattenComponents(oldSnapshot);
            Map<String, SerializedComponent> newComponentsFlat = flattenComponents(newSnapshot);

            // Find new components
            for (String componentKey : newComponentsFlat.keySet()) {
                if (!oldComponentsFlat.containsKey(componentKey)) {
                    newComponents.add(componentKey);
                }
            }

            // Find removed components
            for (String componentKey : oldComponentsFlat.keySet()) {
                if (!newComponentsFlat.containsKey(componentKey)) {
                    removedComponents.add(componentKey);
                }
            }

            // Find modified components
            for (String componentKey : newComponentsFlat.keySet()) {
                if (oldComponentsFlat.containsKey(componentKey)) {
                    SerializedComponent oldComp = oldComponentsFlat.get(componentKey);
                    SerializedComponent newComp = newComponentsFlat.get(componentKey);

                    if (!componentsEqual(oldComp, newComp)) {
                        modifiedComponents.add(componentKey);
                    }
                }
            }
        }

        return new ComponentComparisonResult(newComponents, removedComponents, modifiedComponents);
    }

    /**
     * Flattens component hierarchy into a single map with container::component keys.
     */
    private static Map<String, SerializedComponent> flattenComponents(ComponentSnapshot snapshot) {
        Map<String, SerializedComponent> flattened = new HashMap<>();

        for (Map.Entry<String, ContainerSnapshot> containerEntry : snapshot.containers.entrySet()) {
            String containerName = containerEntry.getKey();
            ContainerSnapshot container = containerEntry.getValue();

            for (Map.Entry<String, SerializedComponent> componentEntry : container.components.entrySet()) {
                String componentName = componentEntry.getKey();
                SerializedComponent component = componentEntry.getValue();

                String flatKey = containerName + "::" + componentName;
                flattened.put(flatKey, component);
            }
        }

        return flattened;
    }

    /**
     * Compares two serialized components for equality.
     */
    private static boolean componentsEqual(SerializedComponent comp1, SerializedComponent comp2) {
        return Objects.equals(comp1.name, comp2.name) &&
                Objects.equals(comp1.description, comp2.description) &&
                Objects.equals(comp1.technology, comp2.technology) &&
                Objects.equals(comp1.tags, comp2.tags) &&
                Objects.equals(comp1.type, comp2.type) &&
                relationshipsEqual(comp1.relationships, comp2.relationships) &&
                Objects.equals(comp1.metadata, comp2.metadata);
    }

    /**
     * Compares two relationship lists for equality.
     */
    private static boolean relationshipsEqual(List<SerializedRelationship> rels1, List<SerializedRelationship> rels2) {
        if (rels1.size() != rels2.size()) {
            return false;
        }

        // Sort relationships by target for consistent comparison
        rels1.sort(Comparator.comparing(r -> r.target));
        rels2.sort(Comparator.comparing(r -> r.target));

        for (int i = 0; i < rels1.size(); i++) {
            SerializedRelationship r1 = rels1.get(i);
            SerializedRelationship r2 = rels2.get(i);

            if (!Objects.equals(r1.target, r2.target) ||
                    !Objects.equals(r1.description, r2.description) ||
                    !Objects.equals(r1.type, r2.type) ||
                    !Objects.equals(r1.properties, r2.properties)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Prints a summary of component comparison results.
     *
     * @param result The comparison result to summarize
     */
    public static void printComparisonSummary(ComponentComparisonResult result) {
        System.out.println("\n=== COMPONENT CHANGE DETECTION SUMMARY ===");

        if (!result.hasChanges) {
            System.out.println(" No changes detected in component architecture");
            return;
        }

        System.out.println("Changes detected:");

        if (!result.newComponents.isEmpty()) {
            System.out.println("\n NEW COMPONENTS (" + result.newComponents.size() + "):");
            result.newComponents.forEach(comp -> System.out.println("  + " + comp));
        }

        if (!result.removedComponents.isEmpty()) {
            System.out.println("\n🗑 REMOVED COMPONENTS (" + result.removedComponents.size() + "):");
            result.removedComponents.forEach(comp -> System.out.println("  - " + comp));
        }

        if (!result.modifiedComponents.isEmpty()) {
            System.out.println("\n MODIFIED COMPONENTS (" + result.modifiedComponents.size() + "):");
            result.modifiedComponents.forEach(comp -> System.out.println("  ~ " + comp));
        }

        System.out.println("\nTotal changes: " +
                (result.newComponents.size() + result.removedComponents.size() + result.modifiedComponents.size()));
    }
}
