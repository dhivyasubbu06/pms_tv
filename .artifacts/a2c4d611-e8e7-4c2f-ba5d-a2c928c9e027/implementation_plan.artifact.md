# Implementation Plan - Replace Booking Success Dialog with Notification Bar

This plan replaces the full-screen/dialog-based booking success messages with a sleek, single-line notification bar at the bottom of the screen, providing a more non-intrusive user experience.

## User Review Required

> [!IMPORTANT]
> - The new notification will appear as a single-line pill at the bottom of the screen.
> - The existing `BookingSuccessDialogFragment` will be bypassed in all service fragments (Spa, Transportation, etc.).
> - The `BookingConfirmationDialogFragment` will now dismiss immediately upon a successful booking request.

## Proposed Changes

### UI Components

#### [NEW] [layout_notification_bar.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/layout/layout_notification_bar.xml)
- Create a luxury-themed (wine/gold) single-line notification layout.
- Use a pill-shaped background with `wine_dark` and a `gold_accent` border.
- Include a success icon (`ic_check_circle`) and a text label.

#### [NEW] [NotificationUtils.kt](file:///D:/pineTV/PineHotelTV/app/src/main/java/com/pinehotel/hospitality/utils/NotificationUtils.kt)
- Create a utility class with a `showSuccessNotification(view: View, message: String)` function.
- This function will use `Snackbar` with the custom layout to show the notification at the bottom.

---

### Logic Updates

#### [MODIFY] [BookingConfirmationDialogFragment.kt](file:///D:/pineTV/PineHotelTV/app/src/main/java/com/pinehotel/hospitality/fragments/BookingConfirmationDialogFragment.kt)
- Update `showSuccessState()` to immediately `dismiss()` the dialog.
- The internal success message within this dialog will no longer be shown.

#### [MODIFY] [SpaWellnessFragment.kt](file:///D:/pineTV/PineHotelTV/app/src/main/java/com/pinehotel/hospitality/fragments/SpaWellnessFragment.kt)
- Update `observeSubmission()` to call `NotificationUtils.showSuccessNotification` with "Spa reserved successfully" instead of showing `BookingSuccessDialogFragment`.

#### [MODIFY] [TransportationFragment.kt](file:///D:/pineTV/PineHotelTV/app/src/main/java/com/pinehotel/hospitality/fragments/TransportationFragment.kt)
- Update `observeSubmission()` to show the bottom notification.

#### [MODIFY] Other Service Fragments
- Apply similar changes to `HousekeepingFragment`, `BarFragment`, `RoomServiceFragment`, etc., to ensure a consistent experience across all booking types.

## Verification Plan

### Automated Tests
- Build the project to ensure no layout or Kotlin errors.

### Manual Verification
- Deploy the app to a TV/Emulator.
- Go to **Spa & Wellness**, select a slot, and click **CONFIRM BOOKING**.
- Verify that:
  1. The booking dialog closes immediately.
  2. A single-line notification saying "Spa reserved successfully" appears at the bottom.
  3. The notification disappears after a few seconds.
- Repeat for **Transportation** and **Housekeeping** to ensure consistency.
