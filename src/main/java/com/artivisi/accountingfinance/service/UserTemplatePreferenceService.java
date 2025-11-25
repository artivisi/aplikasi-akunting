package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.JournalTemplate;
import com.artivisi.accountingfinance.entity.User;
import com.artivisi.accountingfinance.entity.UserTemplatePreference;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.repository.UserTemplatePreferenceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserTemplatePreferenceService {

    private final UserTemplatePreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final JournalTemplateRepository journalTemplateRepository;

    @Transactional
    public boolean toggleFavorite(String username, UUID templateId) {
        User user = findUserByUsername(username);
        JournalTemplate template = findTemplateById(templateId);

        Optional<UserTemplatePreference> existingPref = preferenceRepository
                .findByUserIdAndJournalTemplateId(user.getId(), templateId);

        if (existingPref.isPresent()) {
            UserTemplatePreference pref = existingPref.get();
            pref.toggleFavorite();
            preferenceRepository.save(pref);
            return pref.getIsFavorite();
        } else {
            UserTemplatePreference newPref = new UserTemplatePreference(user, template);
            newPref.setIsFavorite(true);
            preferenceRepository.save(newPref);
            return true;
        }
    }

    @Transactional
    public void recordUsage(String username, UUID templateId) {
        User user = findUserByUsername(username);
        JournalTemplate template = findTemplateById(templateId);

        Optional<UserTemplatePreference> existingPref = preferenceRepository
                .findByUserIdAndJournalTemplateId(user.getId(), templateId);

        if (existingPref.isPresent()) {
            UserTemplatePreference pref = existingPref.get();
            pref.recordUsage();
            preferenceRepository.save(pref);
        } else {
            UserTemplatePreference newPref = new UserTemplatePreference(user, template);
            newPref.recordUsage();
            preferenceRepository.save(newPref);
        }
    }

    public List<JournalTemplate> getFavorites(String username) {
        User user = findUserByUsername(username);
        return preferenceRepository.findFavoritesByUserId(user.getId()).stream()
                .map(UserTemplatePreference::getJournalTemplate)
                .toList();
    }

    public List<JournalTemplate> getRecentlyUsed(String username, int limit) {
        User user = findUserByUsername(username);
        return preferenceRepository.findRecentlyUsedByUserId(user.getId(), PageRequest.of(0, limit)).stream()
                .map(UserTemplatePreference::getJournalTemplate)
                .toList();
    }

    public List<JournalTemplate> getMostUsed(String username, int limit) {
        User user = findUserByUsername(username);
        return preferenceRepository.findMostUsedByUserId(user.getId(), PageRequest.of(0, limit)).stream()
                .map(UserTemplatePreference::getJournalTemplate)
                .toList();
    }

    public Set<UUID> getFavoriteTemplateIds(String username) {
        User user = findUserByUsername(username);
        return Set.copyOf(preferenceRepository.findFavoriteTemplateIdsByUserId(user.getId()));
    }

    public boolean isFavorite(String username, UUID templateId) {
        User user = findUserByUsername(username);
        Optional<UserTemplatePreference> pref = preferenceRepository
                .findByUserIdAndJournalTemplateId(user.getId(), templateId);
        return pref.map(UserTemplatePreference::getIsFavorite).orElse(false);
    }

    public Optional<UserTemplatePreference> getPreference(String username, UUID templateId) {
        User user = findUserByUsername(username);
        return preferenceRepository.findByUserIdAndJournalTemplateId(user.getId(), templateId);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    private JournalTemplate findTemplateById(UUID templateId) {
        return journalTemplateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found: " + templateId));
    }
}
