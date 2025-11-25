# TODO: Template Enhancements (1.7)

Improve template discoverability and user experience.

**Reference:** `docs/06-implementation-plan.md` section 1.7

## Dependencies

- Journal Templates (1.4) ✅ Complete

---

## Current State Analysis

### Already Implemented

| Feature | Location | Notes |
|---------|----------|-------|
| Global favorite flag | `journal_templates.is_favorite` | Template-level, not per-user |
| Usage tracking | `journal_templates.usage_count` | Incremented on template execution |
| Last used timestamp | `journal_templates.last_used_at` | Updated on template execution |
| Category filter | `templates/list.html` | Filter by INCOME/EXPENSE/etc |
| Favorite star display | `templates/list.html` | Shows star if `isFavorite` |

### Missing Features

| Feature | Description |
|---------|-------------|
| Template tags | Categorize templates with user-defined tags |
| User-specific favorites | Per-user favorites (not global) |
| Search functionality | Text search on name/description |
| Recently used list | Quick access to recently used templates |

---

## Implementation Plan

### 1. Database Schema Changes

Modify `V003__create_journal_templates.sql` to add:

```sql
-- Template Tags
CREATE TABLE journal_template_tags (
    id UUID PRIMARY KEY,
    id_journal_template UUID NOT NULL REFERENCES journal_templates(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_template_tag UNIQUE (id_journal_template, tag)
);

CREATE INDEX idx_jtt_template ON journal_template_tags(id_journal_template);
CREATE INDEX idx_jtt_tag ON journal_template_tags(tag);

-- User Template Preferences (per-user favorites and usage)
CREATE TABLE user_template_preferences (
    id UUID PRIMARY KEY,
    id_user UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    id_journal_template UUID NOT NULL REFERENCES journal_templates(id) ON DELETE CASCADE,
    is_favorite BOOLEAN NOT NULL DEFAULT FALSE,
    last_used_at TIMESTAMP,
    use_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT uk_user_template UNIQUE (id_user, id_journal_template)
);

CREATE INDEX idx_utp_user ON user_template_preferences(id_user);
CREATE INDEX idx_utp_template ON user_template_preferences(id_journal_template);
CREATE INDEX idx_utp_favorite ON user_template_preferences(id_user, is_favorite);
CREATE INDEX idx_utp_last_used ON user_template_preferences(id_user, last_used_at DESC);
```

### 2. Entity Classes

#### JournalTemplateTag Entity
- [ ] Create `entity/JournalTemplateTag.java`
  - `id` (UUID)
  - `journalTemplate` (ManyToOne)
  - `tag` (String, max 50)
  - `createdAt` (LocalDateTime)

#### UserTemplatePreference Entity
- [ ] Create `entity/UserTemplatePreference.java`
  - `id` (UUID)
  - `user` (ManyToOne)
  - `journalTemplate` (ManyToOne)
  - `isFavorite` (Boolean)
  - `lastUsedAt` (LocalDateTime)
  - `useCount` (Integer)

#### Update JournalTemplate Entity
- [ ] Add `tags` relationship (OneToMany to JournalTemplateTag)
- [ ] Keep existing global `isFavorite`, `usageCount`, `lastUsedAt` for backward compatibility

### 3. Repository Layer

- [ ] Create `JournalTemplateTagRepository.java`
  - `findByJournalTemplateId(UUID templateId)`
  - `findDistinctTags()` - for tag autocomplete
  - `findTemplatesByTag(String tag)`

- [ ] Create `UserTemplatePreferenceRepository.java`
  - `findByUserIdAndJournalTemplateId(UUID userId, UUID templateId)`
  - `findByUserIdAndIsFavoriteTrue(UUID userId)`
  - `findByUserIdOrderByLastUsedAtDesc(UUID userId, Pageable pageable)`
  - `findByUserIdOrderByUseCountDesc(UUID userId, Pageable pageable)`

### 4. Service Layer

#### Update JournalTemplateService
- [ ] Add `search(String query)` - search by name/description
- [ ] Add `findByTag(String tag)`
- [ ] Add `addTag(UUID templateId, String tag)`
- [ ] Add `removeTag(UUID templateId, String tag)`
- [ ] Add `getDistinctTags()` - for tag autocomplete

#### Create UserTemplatePreferenceService
- [ ] `toggleFavorite(UUID userId, UUID templateId)` - toggle per-user favorite
- [ ] `recordUsage(UUID userId, UUID templateId)` - update lastUsedAt and useCount
- [ ] `getFavorites(UUID userId)` - get user's favorite templates
- [ ] `getRecentlyUsed(UUID userId, int limit)` - get recently used templates
- [ ] `getMostUsed(UUID userId, int limit)` - get most used templates

### 5. Controller Updates

#### JournalTemplateController
- [ ] Add `GET /templates?search={query}` - search endpoint
- [ ] Add `GET /templates?tag={tag}` - filter by tag
- [ ] Add `POST /templates/{id}/tags` - add tag
- [ ] Add `DELETE /templates/{id}/tags/{tag}` - remove tag
- [ ] Add `POST /templates/{id}/favorite` - toggle user favorite
- [ ] Add `GET /templates/favorites` - user's favorites
- [ ] Add `GET /templates/recent` - recently used

### 6. UI Updates

#### Template List Page (`templates/list.html`)
- [ ] Add search input field
- [ ] Add tag filter dropdown/chips
- [ ] Add "Favorites" quick filter button
- [ ] Add "Recently Used" section at top
- [ ] Update favorite star to be clickable (toggle per-user)
- [ ] Show tags on template cards

#### Template Form Page (`templates/form.html`)
- [ ] Add tags input field (with autocomplete)
- [ ] Show existing tags as removable chips

#### Template Detail Page (`templates/detail.html`)
- [ ] Display tags
- [ ] Show favorite toggle button
- [ ] Show usage statistics

### 7. Playwright Tests

- [ ] Test search functionality
- [ ] Test tag CRUD operations
- [ ] Test favorite toggle
- [ ] Test recently used list
- [ ] Test tag filter

---

## TODO Checklist

### Phase 1: Database & Backend
- [ ] Modify V003 migration to add new tables
- [ ] Create JournalTemplateTag entity
- [ ] Create UserTemplatePreference entity
- [ ] Create repositories
- [ ] Update JournalTemplateService with search
- [ ] Create UserTemplatePreferenceService

### Phase 2: API Endpoints
- [ ] Search endpoint
- [ ] Tag CRUD endpoints
- [ ] User favorite endpoints
- [ ] Recently used endpoint

### Phase 3: UI
- [ ] Search input on list page
- [ ] Tag filter on list page
- [ ] Clickable favorite star
- [ ] Recently used section
- [ ] Tags on template cards
- [ ] Tag input on form page

### Phase 4: Tests
- [ ] Unit tests for services
- [ ] Playwright functional tests

---

## Notes

### Migration Strategy

Since modifying V003, ensure:
1. New tables are added at the end of the file
2. No changes to existing table structure
3. New seed data (if any) added after existing seeds

### Backward Compatibility

- Keep global `is_favorite`, `usage_count`, `last_used_at` on `journal_templates`
- These serve as "default" values when no user preference exists
- User preferences override global values when present

### Search Implementation

Use PostgreSQL full-text search or simple ILIKE:
```sql
WHERE template_name ILIKE '%query%'
   OR description ILIKE '%query%'
```

For better performance with large datasets, consider adding:
```sql
CREATE INDEX idx_jt_name_trgm ON journal_templates USING gin (template_name gin_trgm_ops);
```
(Requires pg_trgm extension)

---

## Current Status

**Status:** Not Started

**Next Steps:**
1. Modify V003 migration to add new tables
2. Create entity classes
3. Implement service layer
