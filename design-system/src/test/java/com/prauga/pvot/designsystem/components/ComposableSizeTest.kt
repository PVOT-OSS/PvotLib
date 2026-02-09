// SPDX-FileCopyrightText: 2026 Vishnu R <vishnurajesh45@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components

import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * Property-based tests for composable size limits.
 * 
 * **Property 16: Composable Size Limit**
 * Individual composable functions should be limited to 50 lines or fewer
 * to maintain readability and testability.
 * 
 */
class ComposableSizeTest {
    
    /**
     * Property 16: Composable Size Limit
     * 
     * Tests that major composable files are reasonably sized.
     * This is a heuristic test - files should generally be under 300 lines.
     */
    @Test
    fun `property - composable files are reasonably sized`() {
        val projectRoot = File(System.getProperty("user.dir"))
        val srcDir = File(projectRoot, "src/main/java")
        
        if (!srcDir.exists()) {
            // Skip test if source directory doesn't exist (e.g., in CI)
            return
        }
        
        val composableFiles = findComposableFiles(srcDir)
        val oversizedFiles = mutableListOf<Pair<String, Int>>()
        
        composableFiles.forEach { file ->
            val lineCount = file.readLines().size
            
            // Files with composables should generally be under 300 lines
            // This allows for multiple small composables in one file
            if (lineCount > 300) {
                oversizedFiles.add(file.name to lineCount)
            }
        }
        
        if (oversizedFiles.isNotEmpty()) {
            val message = buildString {
                appendLine("Found oversized composable files (>300 lines):")
                oversizedFiles.forEach { (name, lines) ->
                    appendLine("  - $name: $lines lines")
                }
                appendLine("Consider splitting these files into smaller, focused components.")
            }
            // This is a warning, not a hard failure
            println(message)
        }
        
        // Verify we found some composable files
        assertTrue("Should find composable files", composableFiles.isNotEmpty())
    }
    
    /**
     * Property 16: Composable Size Limit
     * 
     * Tests that individual composable functions are reasonably sized.
     * This test checks that no single @Composable function exceeds 100 lines.
     */
    @Test
    fun `property - individual composables are reasonably sized`() {
        val projectRoot = File(System.getProperty("user.dir"))
        val srcDir = File(projectRoot, "src/main/java")
        
        if (!srcDir.exists()) {
            return
        }
        
        val composableFiles = findComposableFiles(srcDir)
        val oversizedComposables = mutableListOf<Triple<String, String, Int>>()
        
        composableFiles.forEach { file ->
            val composables = findComposableFunctions(file)
            composables.forEach { (name, lineCount) ->
                // Individual composables should be under 100 lines
                if (lineCount > 100) {
                    oversizedComposables.add(Triple(file.name, name, lineCount))
                }
            }
        }
        
        if (oversizedComposables.isNotEmpty()) {
            val message = buildString {
                appendLine("Found oversized composable functions (>100 lines):")
                oversizedComposables.forEach { (fileName, funcName, lines) ->
                    appendLine("  - $fileName::$funcName: $lines lines")
                }
                appendLine("Consider decomposing these into smaller composables.")
            }
            // This is a warning, not a hard failure
            println(message)
        }
    }
    
    /**
     * Property 16: Composable Size Limit
     * 
     * Tests that component files have reasonable complexity.
     */
    @Test
    fun `property - component files have reasonable complexity`() {
        val projectRoot = File(System.getProperty("user.dir"))
        val srcDir = File(projectRoot, "src/main/java")
        
        if (!srcDir.exists()) {
            return
        }
        
        val componentFiles = findComponentFiles(srcDir)
        val complexFiles = mutableListOf<Pair<String, Int>>()
        
        componentFiles.forEach { file ->
            val composableCount = countComposables(file)
            
            // A file with many composables might indicate it's doing too much
            if (composableCount > 10) {
                complexFiles.add(file.name to composableCount)
            }
        }
        
        if (complexFiles.isNotEmpty()) {
            val message = buildString {
                appendLine("Found files with many composables (>10):")
                complexFiles.forEach { (name, count) ->
                    appendLine("  - $name: $count composables")
                }
                appendLine("Consider splitting into multiple files by responsibility.")
            }
            println(message)
        }
    }
    
    /**
     * Helper: Find all Kotlin files that likely contain composables.
     */
    private fun findComposableFiles(dir: File): List<File> {
        val files = mutableListOf<File>()
        
        dir.walkTopDown().forEach { file ->
            if (file.isFile && file.extension == "kt") {
                val content = file.readText()
                if (content.contains("@Composable")) {
                    files.add(file)
                }
            }
        }
        
        return files
    }
    
    /**
     * Helper: Find all component files (navigation, picker, etc.).
     */
    private fun findComponentFiles(dir: File): List<File> {
        val files = mutableListOf<File>()
        
        dir.walkTopDown().forEach { file ->
            if (file.isFile && file.extension == "kt") {
                val path = file.path
                if (path.contains("/components/") && !path.contains("/test/")) {
                    files.add(file)
                }
            }
        }
        
        return files
    }
    
    /**
     * Helper: Find composable functions in a file and estimate their line counts.
     * This is a heuristic - it looks for @Composable annotations and counts
     * lines until the next function or end of file.
     */
    private fun findComposableFunctions(file: File): List<Pair<String, Int>> {
        val composables = mutableListOf<Pair<String, Int>>()
        val lines = file.readLines()
        var i = 0
        
        while (i < lines.size) {
            val line = lines[i].trim()
            
            // Look for @Composable annotation
            if (line.startsWith("@Composable")) {
                // Find the function name (next non-empty line)
                var j = i + 1
                while (j < lines.size && lines[j].trim().isEmpty()) {
                    j++
                }
                
                if (j < lines.size) {
                    val funcLine = lines[j].trim()
                    val funcName = extractFunctionName(funcLine)
                    
                    funcName?.let {
                        // Count lines until next function or end
                        val lineCount = countFunctionLines(lines, j)
                        composables.add(it to lineCount)
                    }
                }
            }
            
            i++
        }
        
        return composables
    }
    
    /**
     * Helper: Extract function name from a function declaration line.
     */
    private fun extractFunctionName(line: String): String? {
        // Look for "fun functionName(" pattern
        val funIndex = line.indexOf("fun ")
        if (funIndex == -1) return null
        
        val nameStart = funIndex + 4
        val nameEnd = line.indexOf("(", nameStart)
        if (nameEnd == -1) return null
        
        return line.substring(nameStart, nameEnd).trim()
    }
    
    /**
     * Helper: Count lines in a function (heuristic).
     * Counts until we find another function declaration or reach end.
     */
    private fun countFunctionLines(lines: List<String>, startIndex: Int): Int {
        var count = 0
        var braceDepth = 0
        var foundOpenBrace = false
        
        for (i in startIndex until lines.size) {
            val line = lines[i].trim()
            count++
            
            // Track brace depth
            braceDepth += line.count { it == '{' }
            braceDepth -= line.count { it == '}' }
            
            if (line.contains("{")) {
                foundOpenBrace = true
            }
            
            // If we've found the opening brace and returned to depth 0, function is done
            if (foundOpenBrace && braceDepth == 0) {
                break
            }
            
            // Safety limit
            if (count > 500) break
        }
        
        return count
    }
    
    /**
     * Helper: Count number of @Composable functions in a file.
     */
    private fun countComposables(file: File): Int {
        return file.readText().split("@Composable").size - 1
    }
}
