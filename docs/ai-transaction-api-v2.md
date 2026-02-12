# AI-Assisted Transaction API v2 - Direct Posting

## Overview

Version 2 of the AI-Assisted Transaction API introduces **direct transaction posting**, allowing AI assistants to bypass the draft workflow after receiving user approval in a client-side consultation flow.

### Key Changes from v1

- **v1 (Draft Flow)**: AI → Create Draft → User approves in app → Transaction posted
- **v2 (Direct Flow)**: AI → User approves in CLI → API posts transaction directly

**User Experience:**
```
User: [drops receipt image]

AI: 📄 Analyzed receipt:
    • Merchant: Starbucks Grand Indonesia
    • Amount: 75,000 IDR
    • Date: 2026-02-11
    • Template: "Meal Expense" (matched by keywords)

    Post transaction? (yes/edit/different template/cancel)

User: yes

AI: ✓ Posted transaction TRX-2026-0123
    [calls POST /api/transactions]
```

---

## New Features

### 1. Enhanced Template Metadata for AI Matching

Journal templates now include semantic metadata to help AI assistants select the correct template:

```sql
-- New columns in journal_templates table
semantic_description TEXT    -- Human-readable explanation
keywords TEXT[]              -- Searchable keywords (lowercase)
example_merchants TEXT[]     -- Example merchant names
typical_amount_min NUMERIC   -- Typical min amount (optional)
typical_amount_max NUMERIC   -- Typical max amount (optional)
merchant_patterns TEXT[]     -- Regex patterns for matching
```

**Example Template Metadata:**

```json
{
  "id": "uuid",
  "name": "Meal Expense",
  "category": "EXPENSE",
  "description": "Recording meal and beverage expenses",
  "semanticDescription": "Use this template when recording expenses for meals, snacks, coffee, or any food and beverage purchases for business purposes. This includes restaurant bills, coffee shop receipts, catering for meetings, and team lunches.",
  "keywords": ["meal", "food", "beverage", "coffee", "restaurant", "lunch", "dinner", "snack", "catering", "makan", "minum", "kopi"],
  "exampleMerchants": ["Starbucks", "McDonald's", "KFC", "Warung Makan", "Restoran", "Cafe"],
  "typicalAmountMin": 10000,
  "typicalAmountMax": 500000,
  "merchantPatterns": [".*coffee.*", ".*cafe.*", ".*restaurant.*", ".*makan.*"]
}
```

### 2. Direct Transaction Posting Endpoint

**POST /api/transactions**

Create and post transaction directly after user approval.

**Request:**
```json
{
  "templateId": "uuid",
  "merchant": "Starbucks Grand Indonesia",
  "amount": 75000,
  "transactionDate": "2026-02-11",
  "currency": "IDR",
  "description": "Team lunch meeting",
  "category": "Food & Beverage",
  "items": ["Caffe Latte", "Croissant"],
  "source": "claude-code",
  "userApproved": true
}
```

**Response (201 Created):**
```json
{
  "transactionId": "uuid",
  "transactionNumber": "TRX-2026-0123",
  "status": "POSTED",
  "merchant": "Starbucks Grand Indonesia",
  "amount": 75000,
  "transactionDate": "2026-02-11",
  "description": "Team lunch meeting",
  "journalEntries": [
    {
      "journalNumber": "JE-2026-0123-01",
      "accountCode": "5100",
      "accountName": "Beban Makan",
      "debitAmount": 75000,
      "creditAmount": 0
    },
    {
      "journalNumber": "JE-2026-0123-02",
      "accountCode": "1100",
      "accountName": "Kas",
      "debitAmount": 0,
      "creditAmount": 75000
    }
  ]
}
```

### 3. Template Management API

**GET /api/templates**
List all templates with enhanced metadata.

**GET /api/templates/{id}**
Get single template with full metadata.

**POST /api/templates**
Create new template (requires authentication).

**PUT /api/templates/{id}**
Update template metadata (requires authentication).

**DELETE /api/templates/{id}**
Soft delete template (requires authentication).

---

## AI Assistant Implementation Guide

### Step 1: Load Template Catalog

```bash
curl http://localhost:8080/api/templates \
  -H "Authorization: Bearer {token}"
```

Cache the template metadata locally. AI should use:
- `semanticDescription` for understanding when to use each template
- `keywords` for keyword matching
- `exampleMerchants` for merchant name matching
- `typicalAmountMin`/`Max` for amount range validation
- `merchantPatterns` for regex matching

### Step 2: Analyze Receipt/Text

When user provides a receipt or text:

1. **Extract data** (AI's job):
   - Merchant name
   - Amount
   - Date
   - Items (if receipt)
   - Category hint

2. **Match template** (AI's job):
   - Compare merchant name with `exampleMerchants`
   - Match extracted category with template `keywords`
   - Validate amount is within `typicalAmountMin`/`Max` range
   - Apply `merchantPatterns` regex
   - Use `semanticDescription` for semantic similarity

3. **Select best template** with confidence score

### Step 3: Consult User

Present to user with options:

```
📄 Analyzed receipt:
   • Merchant: Starbucks Grand Indonesia
   • Amount: 75,000 IDR
   • Date: 2026-02-11
   • Items: Caffe Latte, Croissant
   • Template: "Meal Expense"

Options:
✓ Post transaction
✏️ Edit details
🔄 Choose different template
✗ Cancel
```

### Step 4: Post Transaction

If user approves, call POST /api/transactions:

```javascript
const response = await fetch('http://localhost:8080/api/transactions', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${accessToken}`
  },
  body: JSON.stringify({
    templateId: selectedTemplate.id,
    merchant: "Starbucks Grand Indonesia",
    amount: 75000,
    transactionDate: "2026-02-11",
    description: "Team lunch meeting",
    category: "Food & Beverage",
    items: ["Caffe Latte", "Croissant"],
    source: "claude-code",
    userApproved: true
  })
});

const transaction = await response.json();
console.log(`✓ Posted transaction ${transaction.transactionNumber}`);
```

---

## Authentication

All API endpoints require authentication via OAuth 2.0 Device Flow (RFC 8628).

### Device Flow Steps

1. **Request device code:**
   ```bash
   POST /api/device/code
   {
     "clientId": "claude-code"
   }
   ```

2. **User authorizes in browser:**
   - Visit `/device?code={userCode}`
   - Login and approve

3. **Poll for access token:**
   ```bash
   POST /api/device/token
   {
     "deviceCode": "{deviceCode}"
   }
   ```

4. **Use access token:**
   ```bash
   curl http://localhost:8080/api/transactions \
     -H "Authorization: Bearer {accessToken}"
   ```

---

## Examples

### Example 1: Coffee Shop Receipt

```javascript
// AI extracts data
const receipt = {
  merchant: "Starbucks Central Park",
  amount: 85000,
  date: "2026-02-12",
  items: ["Caffe Latte Grande", "Blueberry Muffin"]
};

// AI matches template
const templates = await fetchTemplates();
const matched = matchTemplate(receipt, templates);
// Result: "Meal Expense" (confidence: 0.95)

// User approves
const approved = await consultUser(receipt, matched);
if (approved) {
  const transaction = await postTransaction({
    templateId: matched.id,
    merchant: receipt.merchant,
    amount: receipt.amount,
    transactionDate: receipt.date,
    description: "Coffee and snack at Starbucks",
    items: receipt.items,
    source: "claude-code",
    userApproved: true
  });

  console.log(`✓ Posted ${transaction.transactionNumber}`);
}
```

### Example 2: Utility Bill (Text Input)

```javascript
// User types: "Bayar listrik PLN 350 ribu"

// AI parses text
const parsed = {
  merchant: "PLN",
  amount: 350000,
  date: today(),
  category: "Utilities"
};

// AI matches template by keywords
const matched = matchByKeywords(parsed, templates);
// Result: "Utility Payment" (confidence: 0.90)

// User approves
if (await consultUser(parsed, matched)) {
  await postTransaction({
    templateId: matched.id,
    merchant: "PLN",
    amount: 350000,
    transactionDate: today(),
    description: "Pembayaran listrik bulan Januari",
    category: "Utilities",
    source: "claude-code",
    userApproved: true
  });
}
```

---

## Comparison: v1 vs v2

| Feature | v1 (Draft Flow) | v2 (Direct Flow) |
|---------|----------------|------------------|
| **API Endpoints** | POST /api/drafts/from-receipt<br>POST /api/drafts/{id}/approve | POST /api/transactions |
| **User Approval** | In web UI | In AI assistant (CLI/chat) |
| **Template Selection** | Server suggests, user picks in UI | AI pre-selects, user confirms in CLI |
| **Flow** | AI → Draft → User approves in web → Transaction | AI → User approves in CLI → Transaction |
| **Template Metadata** | Basic (name, description) | Enhanced (semantic, keywords, examples) |
| **Use Case** | Low confidence, needs review | High confidence, user approved |

**When to use v1 (Draft Flow):**
- Low confidence OCR/parsing (< 85%)
- Complex receipts needing manual review
- Multiple items requiring account selection

**When to use v2 (Direct Flow):**
- High confidence extraction (> 90%)
- User has reviewed and approved in CLI
- Simple transactions (one merchant, one amount)

---

## Migration Guide

Existing AI integrations using v1 (draft flow) continue to work. To adopt v2:

1. **Fetch template catalog** at startup:
   ```javascript
   const templates = await fetch('/api/templates').then(r => r.json());
   ```

2. **Implement template matching logic** using enhanced metadata

3. **Add client-side consultation UI** (Yes/Edit/Different Template/Cancel)

4. **Call POST /api/transactions** after user approval

5. **Handle errors** (validation, authentication, posting failures)

---

## Security

- **Authentication**: Required for all endpoints (Bearer token via Device Flow)
- **Authorization**: Users can only post transactions for their own company
- **Rate Limiting**: 10 drafts/min, 30 transactions/min per IP
- **Audit Logging**: All API calls logged with source, user, and details
- **Input Validation**: Amount > 0, date not in future, template exists

---

## Error Handling

**400 Bad Request** - Invalid input:
```json
{
  "error": "INVALID_REQUEST",
  "message": "Transaction date cannot be in the future",
  "field": "transactionDate"
}
```

**401 Unauthorized** - Missing/invalid token:
```json
{
  "error": "unauthorized",
  "message": "Authentication required"
}
```

**404 Not Found** - Template doesn't exist:
```json
{
  "error": "NOT_FOUND",
  "message": "Template not found with id: {uuid}"
}
```

**500 Internal Server Error** - Server error:
```json
{
  "error": "INTERNAL_ERROR",
  "message": "Failed to post transaction"
}
```

---

## Future Enhancements

1. **Batch Transaction Posting** - POST /api/transactions/batch
2. **Webhook Notifications** - Notify when transaction posted
3. **AI Confidence Feedback Loop** - Improve template matching over time
4. **Multi-Currency Support** - Automatic currency conversion
5. **PDF Receipt Support** - AI extracts from PDF invoices
6. **Bank Statement Import** - Bulk import from PDF/CSV

---

## Support

For questions or issues:
- GitHub: https://github.com/artivisi/accounting-finance/issues
- Documentation: https://docs.example.com/api/v2
- Email: support@artivisi.com
