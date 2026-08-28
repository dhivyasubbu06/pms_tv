# Rectify Missing Service Images and Reduce Transportation Dialog Height

This plan addresses the missing images for "Bar & Lounge" and "Restaurant Reservation" in the services dashboard, and reduces the height of the Transportation booking dialog to be more compact, similar to the Housekeeping dialog.

## Proposed Changes

### 1. Fix Service Images

#### [MODIFY] [ServicesViewModel.kt](file:///D:/pineTV/PineHotelTV/app/src/main/java/com/pinehotel/hospitality/viewmodels/ServicesViewModel.kt)
- Ensure "Bar & Lounge" and "Restaurant Reservation" tiles have valid image URLs.
- I will explicitly check for these titles and provide known working image paths if they are missing or using placeholder icons.

### 2. Reduce Transportation Dialog Height

#### [MODIFY] [dialog_transport_booking.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/layout/dialog_transport_booking.xml)
- Reduce root layout padding from `20dp` to `16dp`.
- Reduce `btnConfirm` height from `54dp` to `48dp`.
- Reduce `etOptionalField` minHeight from `44dp` to `40dp`.
- Reduce margins between dynamic fields from `8dp` to `6dp`.
- Reduce title and info text sizes slightly to save vertical space.

## Verification Plan

### Manual Verification
1. **Service Dashboard**: Open the app and navigate to the Services screen. Verify that "Bar & Lounge" and "Restaurant Reservation" now show photos instead of cloche icons.
2. **Transportation Dialog**: Navigate to Services > Transportation, select a service (e.g., Airport Pickup) and a slot. Verify that the booking dialog appears more compact and fits better on the screen, matching the verticality of the Housekeeping dialog.
