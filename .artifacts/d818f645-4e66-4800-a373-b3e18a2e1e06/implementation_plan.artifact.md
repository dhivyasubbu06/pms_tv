# Implementation Plan - UI Updates for Services and Bookings

This plan outlines the UI changes for Housekeeping and Transportation service cards, as well as the update to the Booking confirmation button to align with the dark luxury gold/burgundy theme.

## User Review Required

> [!IMPORTANT]
> - **Transportation Cards**: The request mentions a "TAP TO BOOK" label on the right. I will replace the current quantity stepper in `item_transport_card.xml` with this label to match the new design specification.
> - **Housekeeping Cards**: The new design includes a pill-shaped gold 'REQUEST' button under the description. I will update the existing button style.

## Proposed Changes

### Resources & Theming

#### [MODIFY] [colors.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/values/colors.xml)
- Add `wine_card_bg` (`#2A0E16`) and `burgundy_text` (`#2A0E16` for text on gold).
- Define `gold_gradient_start` (`#D4AF6A`) and `gold_gradient_end` (`#B8860B`).

#### [MODIFY] [selector_dialog_button_primary.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/drawable/selector_dialog_button_primary.xml)
- Replace `rose_pink` with a gold gradient fill.
- Update focused state to maintain the gold theme with a white stroke.

#### [NEW] [bg_gold_pill.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/drawable/bg_gold_pill.xml)
- Create a gold pill-shaped background for the 'REQUEST' buttons.

#### [NEW] [bg_icon_badge_gold.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/drawable/bg_icon_badge_gold.xml)
- Create a gold square/rounded tile background for service icons.

---

### Housekeeping Component

#### [MODIFY] [item_housekeeping_card.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/layout/item_housekeeping_card.xml)
- Set background to `wine_card_bg` (`#2A0E16`).
- Wrap `ivIcon` in a square gold tile.
- Update `tvTitle` to bold white and `tvDescription` to light gray.
- Style `btnRequest` as a gold pill with dark burgundy text.

---

### Transportation Component

#### [MODIFY] [item_transport_card.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/layout/item_transport_card.xml)
- Resize `ivIcon` to 40x40px and place it in a subtle gold badge.
- Set background to `wine_card_bg`.
- Update text styles to match the luxury theme (bold white title, muted gold/gray subtitle).
- Replace the quantity stepper with a 'TAP TO BOOK' label aligned to the right in small gold uppercase text.

---

### Booking Dialog

#### [MODIFY] [dialog_booking_confirmation.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/layout/dialog_booking_confirmation.xml)
- The `btnConfirm` will automatically pick up the new gold styles from `selector_dialog_button_primary`.

## Verification Plan

### Automated Tests
- Run `app:assembleDebug` to ensure no resource errors.

### Manual Verification
- Deploy to emulator/device.
- Navigate to **Services > Housekeeping** to verify card layout, icons, and buttons.
- Navigate to **Services > Transportation** to verify the resized icons and "TAP TO BOOK" labels.
- Trigger a booking dialog (e.g., from Spa or Transportation) to verify the new Gold "CONFIRM BOOKING" button.
