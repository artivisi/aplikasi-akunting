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

### Implemented Features (1.7)

| Feature | Location | Status |
|---------|----------|--------|
| Template tags | `journal_template_tags` table | ✅ Complete |
| User-specific favorites | `user_template_preferences` table | ✅ Complete |
| Search functionality | `JournalTemplateController` | ✅ Complete |
| Recently used list | `templates/list.html` | ✅ Complete |

---

## Implementation Summary

### 1. Database Schema Changes ✅

Added to `V003__create_journal_templates.sql`:

- `journal_template_tags` table with indexes
- `user_template_preferences` table with indexes

### 2. Entity Classes ✅

- [x] `entity/JournalTemplateTag.java` - tag entity
- [x] `entity/UserTemplatePreference.java` - per-user preferences
- [x] Updated `JournalTemplate.java` - added tags relationship

### 3. Repository Layer ✅

- [x] `JournalTemplateTagRepository.java`
- [x] `UserTemplatePreferenceRepository.java`

### 4. Service Layer ✅

- [x] `JournalTemplateService.java` - added tag methods
- [x] `UserTemplatePreferenceService.java` - new service for user preferences

### 5. Controller Updates ✅

**Query Parameters (standard MVC):**
- [x] `GET /templates?search={query}` - search endpoint
- [x] `GET /templates?tag={tag}` - filter by tag
- [x] `GET /templates?favorites=true` - user favorites filter

**HTMX Endpoints (return HTML fragments):**
- [x] `POST /templates/{id}/toggle-favorite` - returns favorite button fragment
- [x] `POST /templates/{id}/tags` - returns tag list fragment
- [x] `POST /templates/{id}/tags/{tag}/delete` - returns tag list fragment

### 6. UI Updates ✅

#### Template List Page (`templates/list.html`)
- [x] Search input field
- [x] Tag filter chips
- [x] "Favorites" quick filter button
- [x] "Recently Used" section
- [x] Clickable favorite star (per-user)
- [x] Tags on template cards

#### Template Detail Page (`templates/detail.html`)
- [x] Display tags
- [x] Add/remove tags functionality
- [x] Tag autocomplete

### 7. Tests ✅

- [x] All 33 existing JournalTemplateTest tests pass
- [x] Search tests pass
- [x] Basic tag and favorite tests added

---

## Current Status

**Status:** ✅ Complete

**Files Modified:**
- `src/main/resources/db/migration/V003__create_journal_templates.sql`
- `src/main/java/com/artivisi/accountingfinance/entity/JournalTemplate.java`
- `src/main/java/com/artivisi/accountingfinance/entity/JournalTemplateTag.java` (new)
- `src/main/java/com/artivisi/accountingfinance/entity/UserTemplatePreference.java` (new)
- `src/main/java/com/artivisi/accountingfinance/repository/JournalTemplateTagRepository.java` (new)
- `src/main/java/com/artivisi/accountingfinance/repository/UserTemplatePreferenceRepository.java` (new)
- `src/main/java/com/artivisi/accountingfinance/service/JournalTemplateService.java`
- `src/main/java/com/artivisi/accountingfinance/service/UserTemplatePreferenceService.java` (new)
- `src/main/java/com/artivisi/accountingfinance/controller/JournalTemplateController.java`
- `src/main/resources/templates/templates/list.html`
- `src/main/resources/templates/templates/detail.html`
- `src/main/resources/templates/fragments/template-favorite-button.html` (new)
- `src/main/resources/templates/fragments/template-tags.html` (new)
- `src/test/java/com/artivisi/accountingfinance/functional/TemplateEnhancementsTest.java` (new)
- `src/test/java/com/artivisi/accountingfinance/functional/page/TemplateListPage.java`
- `src/test/java/com/artivisi/accountingfinance/functional/page/TemplateDetailPage.java`
