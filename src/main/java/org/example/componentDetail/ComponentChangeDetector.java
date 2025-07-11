package org.example.componentDetail;

import com.structurizr.model.Container;

import java.io.IOException;
import java.util.Map;

/**
 * Utility class for detecting component changes and managing component lifecycle
 * in CI/CD pipeline environments.
 * 
 * <p>This utility provides high-level operations for:</p>
 * <ul>
 *   <li>Detecting new components since last scan</li>
 *   <li>Managing component snapshots for version tracking</li>
 *   <li>Triggering actions based on component changes</li>
 *   <li>Supporting CI/CD automation workflows</li>
 * </ul>
 * 
 * @author C4 Model Generator Team
 * @version 1.0
 * @since 2025-07-11
 */
public class ComponentChangeDetector {
    
    /**
     * Performs a complete component change detection workflow.
     * 
     * <p>This method:</p>
     * <ol>
     *   <li>Loads the previous component snapshot (if exists)</li>
     *   <li>Serializes current components to create new snapshot</li>
     *   <li>Compares old and new snapshots to detect changes</li>
     *   <li>Saves the new snapshot with history tracking</li>
     *   <li>Returns detailed change detection results</li>
     * </ol>
     * 
     * @param containers Map of container names to Container objects containing current components
     * @return ComponentSerializationService.ComponentComparisonResult with detailed change information
     * @throws IOException if snapshot saving fails
     */
    public static ComponentSerializationService.ComponentComparisonResult detectChanges(
            Map<String, Container> containers) throws IOException {
        
        System.out.println("\n=== COMPONENT CHANGE DETECTION WORKFLOW ===");
        
        // Step 1: Load previous snapshot
        System.out.println("1. Loading previous component snapshot...");
        ComponentSerializationService.ComponentSnapshot oldSnapshot = 
                ComponentSerializationService.loadLatestSnapshot();
        
        if (oldSnapshot == null) {
            System.out.println("   No previous snapshot found - this will be the initial baseline");
        } else {
            System.out.println("   ✓ Previous snapshot loaded from: " + oldSnapshot.timestamp);
        }
        
        // Step 2: Create new snapshot from current components
        System.out.println("2. Creating new component snapshot...");
        ComponentSerializationService.ComponentSnapshot newSnapshot = 
                ComponentSerializationService.serializeComponents(containers);
        System.out.println("   ✓ New snapshot created with " + 
                         getTotalComponentCount(newSnapshot) + " components");
        
        // Step 3: Compare snapshots
        System.out.println("3. Comparing snapshots for changes...");
        ComponentSerializationService.ComponentComparisonResult result = 
                ComponentSerializationService.compareSnapshots(oldSnapshot, newSnapshot);
        
        // Step 4: Save new snapshot with history
        System.out.println("4. Saving new snapshot...");
        ComponentSerializationService.saveSnapshotWithHistory(newSnapshot);
        
        // Step 5: Print results
        ComponentSerializationService.printComparisonSummary(result);
        
        return result;
    }
    
    /**
     * Performs change detection and returns whether new components were found.
     * 
     * This is a convenience method for CI/CD pipelines that only need to know
     * if new components exist without detailed change information.
     * 
     * @param containers Map of container names to Container objects
     * @return true if new components were detected, false otherwise
     * @throws IOException if snapshot operations fail
     */
    public static boolean hasNewComponents(Map<String, Container> containers) throws IOException {
        ComponentSerializationService.ComponentComparisonResult result = detectChanges(containers);
        return !result.newComponents.isEmpty();
    }
    
    /**
     * Performs change detection and returns whether any changes were found.
     * 
     * @param containers Map of container names to Container objects
     * @return true if any changes (new, modified, or removed components) were detected
     * @throws IOException if snapshot operations fail
     */
    public static boolean hasAnyChanges(Map<String, Container> containers) throws IOException {
        ComponentSerializationService.ComponentComparisonResult result = detectChanges(containers);
        return result.hasChanges;
    }
    
    /**
     * Creates an initial baseline snapshot without comparison.
     * 
     * This method is useful for establishing the first snapshot in a new project
     * or after major architectural changes.
     * 
     * @param containers Map of container names to Container objects
     * @throws IOException if snapshot saving fails
     */
    public static void createBaselineSnapshot(Map<String, Container> containers) throws IOException {
        System.out.println("\n=== CREATING BASELINE COMPONENT SNAPSHOT ===");
        
        ComponentSerializationService.ComponentSnapshot snapshot = 
                ComponentSerializationService.serializeComponents(containers);
        
        ComponentSerializationService.saveSnapshotWithHistory(snapshot);
        
        System.out.println("✓ Baseline snapshot created with " + 
                         getTotalComponentCount(snapshot) + " components");
    }
    
    /**
     * Generates a detailed change report for CI/CD output.
     * 
     * @param result The comparison result to generate a report for
     * @return Formatted string suitable for CI/CD logs or notifications
     */
    public static String generateChangeReport(ComponentSerializationService.ComponentComparisonResult result) {
        if (!result.hasChanges) {
            return "No architectural changes detected.";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("🏗️ Component Architecture Changes Detected:\n\n");
        
        if (!result.newComponents.isEmpty()) {
            report.append("📦 NEW COMPONENTS (").append(result.newComponents.size()).append("):\n");
            result.newComponents.forEach(comp -> report.append("  + ").append(comp).append("\n"));
            report.append("\n");
        }
        
        if (!result.removedComponents.isEmpty()) {
            report.append("🗑️ REMOVED COMPONENTS (").append(result.removedComponents.size()).append("):\n");
            result.removedComponents.forEach(comp -> report.append("  - ").append(comp).append("\n"));
            report.append("\n");
        }
        
        if (!result.modifiedComponents.isEmpty()) {
            report.append("🔄 MODIFIED COMPONENTS (").append(result.modifiedComponents.size()).append("):\n");
            result.modifiedComponents.forEach(comp -> report.append("  ~ ").append(comp).append("\n"));
            report.append("\n");
        }
        
        int totalChanges = result.newComponents.size() + result.removedComponents.size() + result.modifiedComponents.size();
        report.append("Total changes: ").append(totalChanges);
        
        return report.toString();
    }
    
    /**
     * Validates that containers have components before performing change detection.
     * 
     * @param containers Map of containers to validate
     * @return true if at least one container has components
     */
    public static boolean validateContainersHaveComponents(Map<String, Container> containers) {
        if (containers == null || containers.isEmpty()) {
            System.out.println("⚠ Warning: No containers provided for change detection");
            return false;
        }
        
        int totalComponents = 0;
        for (Container container : containers.values()) {
            totalComponents += container.getComponents().size();
        }
        
        if (totalComponents == 0) {
            System.out.println("⚠ Warning: No components found in any container");
            return false;
        }
        
        System.out.println("✓ Validation passed: " + totalComponents + " components found across " + 
                         containers.size() + " containers");
        return true;
    }
    
    /**
     * Counts total components across all containers in a snapshot.
     */
    private static int getTotalComponentCount(ComponentSerializationService.ComponentSnapshot snapshot) {
        return snapshot.containers.values().stream()
                .mapToInt(container -> container.componentCount)
                .sum();
    }
    
    /**
     * Cleans up old snapshot files, keeping only the most recent N snapshots.
     * 
     * @param keepCount Number of recent snapshots to keep (default: 10)
     */
    public static void cleanupOldSnapshots(int keepCount) {
        // Implementation for cleanup - could be added if needed
        System.out.println("Snapshot cleanup not implemented - keeping all snapshots for now");
    }
}
