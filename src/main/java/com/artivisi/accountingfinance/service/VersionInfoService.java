package com.artivisi.accountingfinance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
@Slf4j
public class VersionInfoService {

    public String getGitCommitId() {
        try {
            Process process = Runtime.getRuntime().exec("git rev-parse HEAD");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String commitId = reader.readLine();
            process.waitFor();
            return commitId != null ? commitId : "Unknown";
        } catch (Exception e) {
            log.warn("Failed to get git commit ID: {}", e.getMessage());
            return "Unknown";
        }
    }

    public String getGitCommitShort() {
        try {
            Process process = Runtime.getRuntime().exec("git rev-parse --short HEAD");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String commitId = reader.readLine();
            process.waitFor();
            return commitId != null ? commitId : "Unknown";
        } catch (Exception e) {
            log.warn("Failed to get git commit ID: {}", e.getMessage());
            return "Unknown";
        }
    }

    public String getGitTag() {
        try {
            Process process = Runtime.getRuntime().exec("git describe --tags --exact-match");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String tag = reader.readLine();
            process.waitFor();
            return tag != null && !tag.isEmpty() ? tag : null;
        } catch (Exception e) {
            log.debug("No exact tag found: {}", e.getMessage());
            return null;
        }
    }

    public String getGitBranch() {
        try {
            Process process = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String branch = reader.readLine();
            process.waitFor();
            return branch != null ? branch : "Unknown";
        } catch (Exception e) {
            log.warn("Failed to get git branch: {}", e.getMessage());
            return "Unknown";
        }
    }

    public String getGitCommitDate() {
        try {
            Process process = Runtime.getRuntime().exec("git log -1 --format=%cd --date=iso");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String date = reader.readLine();
            process.waitFor();
            return date != null ? date : "Unknown";
        } catch (Exception e) {
            log.warn("Failed to get git commit date: {}", e.getMessage());
            return "Unknown";
        }
    }
}
