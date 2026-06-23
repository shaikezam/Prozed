<?php
// group issues by project key for the board
$byProject = [];
foreach ($issues as $i) {
    $byProject[$i['projectKey']][] = $i;
}
$typeColors = [
    'EPIC'    => '#8250df',
    'STORY'   => '#1a7f37',
    'TASK'    => '#0969da',
    'SUBTASK' => '#57606a',
    'BUG'     => '#cf222e',
];
$statusColors = [
    'TODO'        => '#57606a',
    'IN_PROGRESS' => '#bf8700',
    'DONE'        => '#1a7f37',
];
function chip($text, $color) {
    return '<span style="background:' . $color . ';color:#fff;padding:2px 8px;border-radius:10px;font-size:0.75em;font-weight:bold;">'
        . htmlspecialchars($text) . '</span>';
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Prozed Task-Tracker - Project Tracker</title>
    <style>
        body { font-family: sans-serif; margin: 30px; background: #f4f6f9; color: #24292f; }
        h1 { margin-bottom: 0; }
        .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
        .card { background: white; padding: 22px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.06); margin-bottom: 20px; }
        h2 { margin-top: 0; font-size: 1.1em; border-bottom: 1px solid #eaecef; padding-bottom: 8px; }
        input, button, select, textarea { padding: 8px; margin: 4px 0; width: 100%; box-sizing: border-box; }
        button { background: #0969da; color: white; border: none; cursor: pointer; border-radius: 5px; font-weight: bold; width: auto; padding: 8px 16px; }
        button.mini { padding: 4px 10px; font-size: 0.8em; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th, td { border-bottom: 1px solid #eaecef; padding: 8px; text-align: left; font-size: 0.9em; vertical-align: middle; }
        th { background: #f6f8fa; }
        .indent { color: #57606a; font-size: 0.85em; }
        a { color: #0969da; text-decoration: none; }
        .row-form { display: flex; gap: 4px; align-items: center; }
        .row-form select { width: auto; }
    </style>
</head>
<body>

    <h1>Prozed Task-Tracker</h1>
    <p><a href="http://localhost:8093" target="_blank">🌐 Open phpMyAdmin DB Management Panel</a></p>

    <div class="grid">
        <div class="card">
            <h2>Create Project</h2>
            <form action="/create-project" method="POST">
                <input type="text" name="project_key" placeholder="Project key (e.g. APP)" required>
                <input type="text" name="name" placeholder="Project name" required>
                <input type="text" name="description" placeholder="Description">
                <button type="submit">Create Project</button>
            </form>
            <table>
                <thead><tr><th>Key</th><th>Name</th><th>Description</th></tr></thead>
                <tbody>
                    <?php if (empty($projects)): ?>
                        <tr><td colspan="3">No projects yet.</td></tr>
                    <?php else: foreach($projects as $p): ?>
                        <tr>
                            <td><strong><?= htmlspecialchars($p['projectKey']) ?></strong></td>
                            <td><?= htmlspecialchars($p['name']) ?></td>
                            <td><?= htmlspecialchars($p['description']) ?></td>
                        </tr>
                    <?php endforeach; endif; ?>
                </tbody>
            </table>
        </div>

        <div class="card">
            <h2>Create Issue</h2>
            <form action="/create-issue" method="POST">
                <select name="project_key" required>
                    <option value="" disabled selected>Select project</option>
                    <?php foreach($projects as $p): ?>
                        <option value="<?= htmlspecialchars($p['projectKey']) ?>"><?= htmlspecialchars($p['projectKey'] . ' - ' . $p['name']) ?></option>
                    <?php endforeach; ?>
                </select>
                <select name="type" required>
                    <option value="EPIC">Epic</option>
                    <option value="STORY">Story</option>
                    <option value="TASK" selected>Task</option>
                    <option value="SUBTASK">Subtask</option>
                    <option value="BUG">Bug</option>
                </select>
                <input type="text" name="summary" placeholder="Summary" required>
                <textarea name="description" placeholder="Description" rows="2"></textarea>
                <input type="text" name="parent_key" placeholder="Parent key (optional, e.g. PROZ-1)">
                <input type="text" name="assignee" placeholder="Assignee (optional)">
                <select name="priority">
                    <option value="HIGHEST">Highest</option>
                    <option value="HIGH">High</option>
                    <option value="MEDIUM" selected>Medium</option>
                    <option value="LOW">Low</option>
                    <option value="LOWEST">Lowest</option>
                </select>
                <button type="submit">Create Issue</button>
            </form>
        </div>
    </div>

    <div class="card">
        <h2>Issue Board</h2>
        <?php if (empty($issues)): ?>
            <p>No issues yet.</p>
        <?php else: foreach($projects as $p): $pk = $p['projectKey']; ?>
            <h3><?= htmlspecialchars($pk . ' - ' . $p['name']) ?></h3>
            <table>
                <thead>
                    <tr><th>Key</th><th>Type</th><th>Summary</th><th>Parent</th><th>Assignee</th><th>Priority</th><th>Status</th><th>Move to</th></tr>
                </thead>
                <tbody>
                    <?php if (empty($byProject[$pk])): ?>
                        <tr><td colspan="8">No issues in this project.</td></tr>
                    <?php else: foreach($byProject[$pk] as $i): ?>
                        <tr>
                            <td><strong><?= htmlspecialchars($i['issueKey']) ?></strong></td>
                            <td><?= chip($i['type'], $typeColors[$i['type']] ?? '#57606a') ?></td>
                            <td>
                                <?php if (!empty($i['parentKey'])): ?><span class="indent">↳ </span><?php endif; ?>
                                <?= htmlspecialchars($i['summary']) ?>
                            </td>
                            <td><?= htmlspecialchars($i['parentKey'] ?? '') ?></td>
                            <td><?= htmlspecialchars($i['assignee'] ?? '') ?></td>
                            <td><?= htmlspecialchars($i['priority'] ?? '') ?></td>
                            <td><?= chip($i['status'], $statusColors[$i['status']] ?? '#57606a') ?></td>
                            <td>
                                <form action="/transition" method="POST" class="row-form">
                                    <input type="hidden" name="issue_key" value="<?= htmlspecialchars($i['issueKey']) ?>">
                                    <select name="status">
                                        <option value="TODO">TODO</option>
                                        <option value="IN_PROGRESS">IN_PROGRESS</option>
                                        <option value="DONE">DONE</option>
                                    </select>
                                    <button type="submit" class="mini">Go</button>
                                </form>
                            </td>
                        </tr>
                    <?php endforeach; endif; ?>
                </tbody>
            </table>
        <?php endforeach; endif; ?>
    </div>

    <div class="card">
        <h2>Activity Feed (via ActiveMQ -> activity-service)</h2>
        <table>
            <thead><tr><th>Issue</th><th>Action</th><th>Detail</th><th>When</th></tr></thead>
            <tbody>
                <?php if (empty($activity)): ?>
                    <tr><td colspan="4">No activity recorded yet.</td></tr>
                <?php else: foreach($activity as $a): ?>
                    <tr>
                        <td><strong><?= htmlspecialchars($a['issueKey'] ?? '') ?></strong></td>
                        <td><?= htmlspecialchars($a['action'] ?? '') ?></td>
                        <td><?= htmlspecialchars($a['detail'] ?? '') ?></td>
                        <td><?= htmlspecialchars($a['loggedAt'] ?? '') ?></td>
                    </tr>
                <?php endforeach; endif; ?>
            </tbody>
        </table>
    </div>

</body>
</html>
