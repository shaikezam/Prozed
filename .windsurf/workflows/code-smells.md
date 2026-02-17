---
description: AI-powered Git auditor for Windsurf. Scans untracked/modified files for smells, security risks, and lint violations. Outputs a prioritized, line-specific findings table without modifying code.
---

# AI Code Smell + Lint Analysis Workflow (Windsurf)

## Purpose

Analyze all untracked and modified Git files using AI-based static analysis (no external linters or tools), detect code smells, lint violations, architectural issues, and risks, and output a structured findings table.

This workflow:
- Detects untracked and modified files using git
- Extracts full content (untracked) and diffs (modified)
- Runs AI-based code smell + lint-style analysis
- Outputs a findings table in Markdown format
- Sorts findings from highest severity to lowest
- Instructs the AI to print severity values in color (environment-rendered)
- Includes file path AND line number for each finding
- Lists scanned files
- Never modifies source code
- Does NOT use external lint tools (AI simulates lint rules)

---

## Execution Flow (Single Run)

### 1. Detect Git Scope

Collect untracked files:


git ls-files --others --exclude-standard


Collect modified files:


git diff --name-only


Optional: filter to supported extensions:


.ts, .tsx, .js, .jsx, .py, .java, .cs, .go, .rb


Merge both lists into a single scan list.

---

### 2. Prepare Analysis Input

For each file:

If file is untracked:
- Include full file content (with line numbers if available)

If file is modified:
- Include:

git diff <file>


Structure the input as:


===== FILE: path/to/file.ts (UNTRACKED) =====
<full content>

===== FILE: path/to/file.ts (MODIFIED DIFF) =====
<git diff output>


---

### 3. AI Code Smell + Lint Analysis

Run AI analysis on the prepared input.

The AI must analyze for:

------------------------------------
A. Code Smells
------------------------------------
- Large functions
- God classes
- Duplicate logic
- Deep nesting
- Long parameter lists
- Magic numbers
- Dead code
- Poor naming
- Tight coupling

------------------------------------
B. Architectural Issues
------------------------------------
- SOLID violations
- Leaky abstractions
- Circular dependencies
- High cyclomatic complexity

------------------------------------
C. Security Risks
------------------------------------
- Hardcoded secrets
- Unsafe deserialization
- SQL injection risk
- Missing input validation
- Unsafe eval usage

------------------------------------
D. Performance Issues
------------------------------------
- N+1 queries
- Blocking operations
- Memory leaks
- Inefficient loops

------------------------------------
E. Reliability Issues
------------------------------------
- Missing error handling
- Unhandled async promises
- Race conditions
- Resource leaks

------------------------------------
F. Lint-Style Violations (AI Simulated)
------------------------------------
Simulate common lint rules such as:

- Unused variables
- Unused imports
- Missing return statements
- Inconsistent naming conventions
- Trailing whitespace
- Missing semicolons (where required)
- Console.log in production code
- Debug statements
- Improper indentation
- Missing null/undefined checks
- Too many function parameters
- Missing default case in switch
- Empty catch blocks
- Shadowed variables

Do NOT run external lint tools.
The AI must infer these issues from the code content.

---

## Severity Levels

- Critical — Security issue, crash risk, data corruption
- High — Major architectural or correctness issue
- Medium — Maintainability, significant lint issue
- Low — Minor lint/style improvement

---

## Rules

- Do NOT modify code
- Do NOT rewrite code
- Only analyze provided files
- If no issues found, output "No issues found"
- Output only the final report
- The findings table MUST be returned in Markdown table format
- Findings MUST be sorted from highest severity to lowest
- The AI MUST print severity values in color using the rendering capabilities of the execution environment (do NOT use HTML span tags)
- If the environment does not support color rendering, print severity labels in uppercase (CRITICAL, HIGH, MEDIUM, LOW)
- Each finding MUST include:
    - Exact file path
    - Specific line number or line range (e.g., 42 or 42–57)
- Findings must be concrete and reference real code from input (no hallucinations)

---

## Final Output Format

The workflow must output exactly:

### AI Code Smell & Lint Analysis Report


Files scanned:
- src/utils/math.ts (Untracked)
- src/services/userService.ts (Modified)


The findings must be presented as a Markdown table, sorted from highest severity to lowest:


| Severity | File Path | Line | Issue Type | What's Wrong | Suggested Fix |
|----------|----------|------|------------|--------------|---------------|
| CRITICAL | src/security/auth.ts | 18 | Security - Hardcoded Secret | API key is stored directly in source code | Move secret to environment variable |
| HIGH | src/services/userService.ts | 45–120 | Code Smell - Large Function | Function exceeds 150 lines and handles multiple responsibilities | Split into smaller focused functions |
| MEDIUM | src/utils/math.ts | 12 | Lint - Magic Number | Hardcoded value 42 without explanation | Replace with named constant |
| LOW | src/helpers/date.ts | 7 | Lint - Naming | Variable name 'd' is not descriptive | Rename to 'currentDate' |


If no issues:


Files scanned:
- src/example.ts (Modified)

No issues found.


---

## Guarantees

- No source files are modified
- No external analysis tools are used
- Only git-detected changes are analyzed
- AI performs both architectural and lint-style reasoning
- Output is deterministic and structured
- Suitable for CI or local execution