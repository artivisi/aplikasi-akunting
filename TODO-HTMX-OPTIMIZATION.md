# TODO: HTMX Partial Rendering Optimization

Optimize existing pages to use HTMX partial rendering for better UX and performance.

**Goal:** Eliminate full page reloads for filter/search/pagination operations.

---

## Pattern to Apply

1. Create Thymeleaf fragment for the content area (grid/table/list)
2. Add HTMX attributes to trigger elements (hx-get, hx-target, hx-swap)
3. Controller returns fragment when request has HX-Request header
4. Preserve URL state with hx-push-url

---

## 1. Template List Search/Filter

**Status:** ⏳ Next

**Current:** Full page reload on search, category filter, tag filter, favorites filter

**Target:** Swap only the template grid, preserve filters in URL

**Files to modify:**
- `src/main/resources/templates/templates/list.html`
- `src/main/resources/templates/fragments/template-grid.html` (new)
- `src/main/java/.../controller/JournalTemplateController.java`

**Implementation:**
- [ ] Extract template grid to fragment (`fragments/template-grid.html`)
- [ ] Add `hx-get` to search form with `hx-target="#template-grid"`
- [ ] Add `hx-get` to category filter links
- [ ] Add `hx-get` to tag filter links
- [ ] Add `hx-get` to favorites filter link
- [ ] Controller: detect HX-Request header, return fragment
- [ ] Add `hx-push-url="true"` to preserve browser history
- [ ] Update tests

---

## 2. Journal List (Buku Besar)

**Status:** ⏳ Pending

**Current:** Full page reload on account select, date change, search, pagination

**Target:** Swap ledger data section, keep filters visible

**Files to modify:**
- `src/main/resources/templates/journals/list.html`
- `src/main/resources/templates/fragments/journal-ledger.html` (new)
- `src/main/java/.../controller/JournalEntryController.java`

**Implementation:**
- [ ] Extract ledger data section to fragment
- [ ] Add `hx-get` to account dropdown (on change)
- [ ] Add `hx-get` to date inputs (on change or button click)
- [ ] Add `hx-get` to search input
- [ ] Add `hx-get` to pagination links
- [ ] Controller: detect HX-Request header, return fragment
- [ ] Add `hx-push-url="true"` for bookmarkable URLs
- [ ] Update tests

---

## 3. Transaction List

**Status:** ⏳ Pending

**Current:** Full page reload on filter/search + `window.location.reload()` after post/delete

**Target:** Swap table on filter, update/remove row on actions

**Files to modify:**
- `src/main/resources/templates/transactions/list.html`
- `src/main/resources/templates/fragments/transaction-table.html` (new)
- `src/main/resources/templates/fragments/transaction-row.html` (new)
- `src/main/java/.../controller/TransactionController.java`

**Implementation:**
- [ ] Extract transaction table to fragment
- [ ] Extract single transaction row to fragment
- [ ] Add `hx-get` to filter form
- [ ] Add `hx-get` to pagination links
- [ ] Convert post button to `hx-post`, swap row with updated status
- [ ] Convert delete button to `hx-delete`, remove row with `hx-swap="delete"`
- [ ] Controller: return appropriate fragments
- [ ] Add `hx-push-url="true"` for filters
- [ ] Update tests

---

## 4. Dashboard KPIs

**Status:** ⏳ Pending (1.3 Dashboard KPIs not yet implemented)

**Target:** Load KPI cards via HTMX on page load or date change

**Files to create:**
- `src/main/resources/templates/fragments/dashboard-kpis.html`
- Update `src/main/resources/templates/dashboard.html`
- `src/main/java/.../controller/DashboardController.java`

**Implementation:**
- [ ] Create KPI cards fragment
- [ ] Add `hx-get` with `hx-trigger="load"` for initial load
- [ ] Add date range selector with `hx-get` on change
- [ ] Controller endpoint returns KPI fragment
- [ ] Add loading indicator (`hx-indicator`)

---

## Technical Notes

### Detecting HTMX Requests

```java
@GetMapping("/templates")
public String list(..., @RequestHeader(value = "HX-Request", required = false) String hxRequest, Model model) {
    // ... populate model ...

    if ("true".equals(hxRequest)) {
        return "fragments/template-grid :: grid";
    }
    return "templates/list";
}
```

### Fragment Structure

```html
<!-- fragments/template-grid.html -->
<div th:fragment="grid" id="template-grid">
    <!-- Grid content here -->
</div>
```

### HTMX Attributes

```html
<form hx-get="/templates"
      hx-target="#template-grid"
      hx-swap="innerHTML"
      hx-push-url="true">
    <!-- Search/filter inputs -->
</form>
```

---

## Dependencies

- HTMX already included in base layout
- Template Enhancements (1.7) provides working HTMX example (favorite toggle, tags)

---

## Testing

Each optimization should:
1. Verify partial content returned for HTMX requests
2. Verify full page returned for regular requests
3. Verify URL updates correctly with filters
4. Verify browser back/forward works
