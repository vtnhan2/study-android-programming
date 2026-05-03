Màn hình 1: Trang chủ (Home Screen)
Prompt này sẽ tạo ra màn hình đầu tiên (bên trái).

Plaintext
**Prompt for Antigravitys: Mobile Home Screen for Bike App**

Please generate Android code (using XML or Jetpack Compose, whichever is preferred) for the modern, clean bike app home screen as shown in the provided image (the leftmost screen).

**General Requirements:**
*   Implement this using Material Design principles.
*   The primary color is an emerald green (use a specific hex code like #009688 or similar).
*   Corner radii for cards should be around 20dp.

**Structure (Top to Bottom):**

1.  **Header:** A simple toolbar with a menu drawer icon on the left. On the right, a notification bell icon with a small red notification dot.
2.  **Greeting:** A large `TextView` (Bold, font size approx 24sp) with the text "Hi Scott G!". Below it, a smaller `TextView` with the search hint: "Search your favourite bike here...".
3.  **Hero Banner:** A large, rounded-corner `CardView` with a green background (#009688).
    *   Inside the card, place a smaller modern motorcycle image (placeholder).
    *   Add text: "Explore Latest" (Bold) and "Bikes with Price" below it.
    *   A small, white, rounded button with text "Explore".
4.  **Most Searched Section:**
    *   A `TextView` titled "The most searched Bikes" (Bold) and a small, clickable `TextView` on the right saying "View all".
    *   A horizontal `RecyclerView`. Each item is a smaller `CardView`.
    *   Card Item Details: Small bike image (placeholder). Text titles like "Honda SP 125", "Hero Splendor Plus". A small heart outline icon. A price placeholder below.
5.  **Recommended Section:**
    *   A `TextView` titled "Recommended Bikes For You" (Bold) and "View all".
    *   Another horizontal `RecyclerView`.
    *   Card Item Details: Small bike image (placeholder). Text titles like "Honda CD 110 Dream", "Hero Passion Pro". A small heart outline icon. A price placeholder below.
6.  **Bottom Navigation Bar:**
    *   A custom `BottomNavigationView` with 4 icons and text labels (Home, Search, Likes, Profile).
    *   The "Home" item should be active, with a solid green background, white icon, and white text. The others should have a greyed-out icon and text.

Please generate the complete XML layout and corresponding Activity or Fragment code, and a custom theme for the colors.
Màn hình 2: Chi tiết Xe (Bike Details Screen)
Prompt này sẽ tạo ra màn hình thứ hai (ở giữa).

Plaintext
**Prompt for Antigravitys: Mobile Bike Details Screen**

Please generate Android code for the detailed bike view screen as shown in the provided image (the middle screen).

**General Requirements:**
*   Maintain the clean, modern Material Design style.
*   Use the same emerald green (#009688 or similar) for key UI elements.

**Structure (Top to Bottom):**

1.  **Toolbar:** A transparent or light toolbar with a back arrow button on the left, the title "Bike Details" centered, and a share icon on the right. All icons and text in black.
2.  **Main Image Section:** A large `ImageView` centered, showing the classic motorcycle ("Classic 350" placeholder).
3.  **Title and Favorite:**
    *   A large, bold `TextView` with the text "Classic 350".
    *   Below it, a smaller `TextView` "By Royal Enfield".
    *   On the right of the title, a circular favorite button containing a heart outline icon.
4.  **Quick Info Cards:** Three small, separate, white, rounded-corner `CardView`s arranged horizontally.
    *   Card 1: A star icon, title "Rating", value "5.0".
    *   Card 2: A currency icon, title "Price", value "₹ 1,84,374*".
    *   Card 3: A wheel icon, title "Variants", value "5 Variants".
5.  **Detail Section:**
    *   A bold `TextView` title "Bike Details".
    *   A main `TextView` with long lorem ipsum descriptive text. (Placeholder text will do).
6.  **Bottom Action (Optional, but implied):** Add a faded-out, rounded action button at the very bottom (perhaps for "Pre-book Now") in green.

Please generate the complete XML layout and corresponding Activity or Fragment code.
Màn hình 3: Menu Bên (Side Menu / Drawer Screen)
Prompt này sẽ tạo ra màn hình thứ ba (bên phải).

Plaintext
**Prompt for Antigravitys: Mobile Side Menu Drawer Screen**

Please generate Android code for the complete side menu drawer view, showing a layered effect, as in the provided image (the rightmost screen).

**General Requirements:**
*   The primary view (the drawer itself) has a solid emerald green background (#009688 or similar).
*   All text and icons on this drawer are white.

**Structure (From Top to Bottom of the Green Drawer):**

1.  **User Profile:**
    *   A circular `ImageView` with a user profil![alt text](image.png)e photo (placeholder).
    *   A bold `TextView` below it with "Welcome John!".
2.  **Menu Items:** A vertical list of navigation items, each with a custom line-art style icon (white) and white text.
    *   List Items: Home Page (highlighted or slightly different), Best Deal Bikes, Notifications, Rate Us, Help Center, Sign Out.
3.  **Layered Look:** The right side of the screen must peek into a version of the Home Screen (showing a partial view of the "Hi Scott G!" header and the "Explore Latest" card). This creates a layered drawer effect. (This will require using a `DrawerLayout` or a custom layout with overlaying fragments).
4.  **Bottom Logo:** At the very bottom of the green menu area, a white logo text: "Insightlancer".

Please generate the complete XML layout, including the `DrawerLayout` structure,