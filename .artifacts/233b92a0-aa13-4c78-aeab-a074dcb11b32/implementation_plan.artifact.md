# Implementation Plan - Launcher UI Update

Update the TV Launcher UI to match the provided hotel hospitality interface image. This involves refining the layout, spacing, and styling of the header, welcome section, experiences panel, menu grid, and footer.

## User Review Required

> [!IMPORTANT]
> The layout will be adjusted to have 5 items in the first row and 4 items in the second row of the menu grid, matching the provided image.
> Spacing will be increased to avoid a "congested" look as requested.

## Proposed Changes

### UI Layout and Styling

#### [MODIFY] [activity_main.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/layout/activity_main.xml)
- Increase top padding and margins for better spacing.
- Update logo crown and text colors to `gold_accent`.
- Adjust clock and date positioning and styling.
- Refine the Wifi Card layout and colors.
- Update `welcomeGroup` spacing.
- Style `tvTagline` to be italic and use `gold_accent`.
- Adjust `experiencesPanel` dimensions and icon color.
- Update bottom action buttons to use gold outlines.

#### [MODIFY] [item_menu_card.xml](file:///D:/pineTV/PineHotelTV/app/src/main/res/layout/item_menu_card.xml)
- Change `cardIcon` tint to `rose_pink`.
- Remove the solid `bg_icon_badge_circle` to match the target's clean icon look.
- Set `app:cardElevation` to `0dp` for a flatter appearance.
- Update title and subtitle fonts and spacing.

### Logic and Data

#### [MODIFY] [MainActivity.kt](file:///D:/pineTV/PineHotelTV/app/src/main/java/com/pinehotel/hospitality/MainActivity.kt)
- Update `menuItems` list to include "HOUSEKEEPING".
- Implement `SpannableString` in `bindGuestContext` to style "Welcome, Guest" (Guest in rose pink and italic).
- Ensure the `GridLayoutManager` `spanSizeLookup` correctly handles the 5+4 grid layout.

## Verification Plan

### Automated Tests
- Build and run the application on a TV emulator or device.
- Verify that the layout matches the provided image in terms of elements and spacing.

### Manual Verification
- Check focus navigation between the 9 grid items and bottom buttons.
- Confirm colors and fonts are consistent with the "Pine Hotel" theme shown in the image.
- Verify clock and date are updating correctly.
