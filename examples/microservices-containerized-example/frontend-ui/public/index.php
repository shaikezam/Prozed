<?php
require __DIR__ . '/../vendor/autoload.php';

Flight::set('flight.views.path', __DIR__ . '/../views');

function httpGet(string $url): string {
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    $response = curl_exec($ch);
    curl_close($ch);
    return $response ?: '';
}

function httpPostJson(string $url, array $data): void {
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type:application/json']);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_exec($ch);
    curl_close($ch);
}

Flight::route('GET /', function() {
    $projects = json_decode(httpGet("http://project-service:8080/projects"), true) ?? [];
    $issues   = json_decode(httpGet("http://issue-service:8080/issues"), true) ?? [];
    $activity = json_decode(httpGet("http://activity-service:8080/activity/history"), true) ?? [];
    Flight::render('dashboard.php', ['projects' => $projects, 'issues' => $issues, 'activity' => $activity]);
});

Flight::route('POST /create-project', function() {
    $d = Flight::request()->data;
    httpPostJson("http://project-service:8080/projects", [
        'projectKey'  => $d->project_key,
        'name'        => $d->name,
        'description' => $d->description,
    ]);
    Flight::redirect('/');
});

Flight::route('POST /create-issue', function() {
    $d = Flight::request()->data;
    httpPostJson("http://issue-service:8080/issues", [
        'projectKey'  => $d->project_key,
        'type'        => $d->type,
        'summary'     => $d->summary,
        'description' => $d->description,
        'parentKey'   => $d->parent_key ?: null,
        'assignee'    => $d->assignee ?: null,
        'priority'    => $d->priority,
    ]);
    Flight::redirect('/');
});

Flight::route('POST /transition', function() {
    $d = Flight::request()->data;
    httpPostJson("http://issue-service:8080/issues/transition", [
        'issueKey' => $d->issue_key,
        'status'   => $d->status,
    ]);
    Flight::redirect('/');
});

Flight::start();
