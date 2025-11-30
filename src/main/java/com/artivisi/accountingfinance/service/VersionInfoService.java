package com.artivisi.accountingfinance.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VersionInfoService {

    @Value("${git.commit.id.full:Unknown}")
    private String gitCommitId;

    @Value("${git.commit.id.abbrev:Unknown}")
    private String gitCommitShort;

    @Value("${git.branch:Unknown}")
    private String gitBranch;

    @Value("${git.commit.time:Unknown}")
    private String gitCommitTime;

    @Value("${git.tags:}")
    private String gitTags;

    @Value("${git.build.time:Unknown}")
    private String buildTime;

    public String getGitCommitId() {
        return gitCommitId;
    }

    public String getGitCommitShort() {
        return gitCommitShort;
    }

    public String getGitTag() {
        if (gitTags != null && !gitTags.isEmpty() && !gitTags.equals("Unknown")) {
            // Tags are comma-separated, return the first one
            String[] tags = gitTags.split(",");
            return tags[0].trim();
        }
        return null;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public String getGitCommitDate() {
        return gitCommitTime;
    }

    public String getBuildTime() {
        return buildTime;
    }
}
