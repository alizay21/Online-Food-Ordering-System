-- ==========================================================
-- 1. FIX PIZZA / BURGER MENU MISMATCH (CRITICAL)
-- The live database has mixed up restaurant IDs.
-- ==========================================================
USE multimeal_db;

-- Fix Smash Burger Bar (ID 7)
UPDATE Menu_Items SET rest_id = 7 WHERE name IN ('Classic Cheeseburger', 'BBQ Bacon Burger');

-- Fix Classic Burger Co (ID 8)
UPDATE Menu_Items SET rest_id = 8 WHERE name IN ('Double Patty Burger', 'Mushroom Swiss Burger');

-- Fix Cheesy Slice (ID 9)
UPDATE Menu_Items SET rest_id = 9 WHERE name IN ('Pepperoni Pizza', 'BBQ Chicken Pizza');

-- Fix New York Pie (ID 10)
UPDATE Menu_Items SET rest_id = 10 WHERE name IN ('Supreme Pizza', 'Meat Lovers Pizza');


-- ==========================================================
-- 2. FIX MISSING IMAGES (Update DB to match your actual files)
-- Your files are mostly .png and don't have '_small' in the name.
-- ==========================================================

-- Desi
UPDATE Menu_Items SET image_path = 'assets/images/menu/chicken_biryani.jpg' WHERE name = 'Chicken Biryani';
UPDATE Menu_Items SET image_path = 'assets/images/menu/beef_pulao.png' WHERE name = 'Beef Pulao';
UPDATE Menu_Items SET image_path = 'assets/images/menu/seekh_kebab.png' WHERE name = 'Seekh Kebab';
-- Note: 'beef_karahi.png' exists but I don't see 'Beef Karahi' in your menu names (you have Chicken Karahi).
-- If you want to use it for Chicken Karahi:
-- UPDATE Menu_Items SET image_path = 'assets/images/menu/beef_karahi.png' WHERE name = 'Chicken Karahi';

-- Chinese
UPDATE Menu_Items SET image_path = 'assets/images/menu/sweet_sour_chicken.png' WHERE name = 'Sweet and Sour Chicken';
UPDATE Menu_Items SET image_path = 'assets/images/menu/kung_pao.png' WHERE name = 'Kung Pao Chicken';
UPDATE Menu_Items SET image_path = 'assets/images/menu/general_tso.png' WHERE name = 'General Tso Chicken';
UPDATE Menu_Items SET image_path = 'assets/images/menu/orange_chicken.png' WHERE name = 'Orange Chicken';

-- Burger
UPDATE Menu_Items SET image_path = 'assets/images/menu/bbq.png' WHERE name = 'BBQ Bacon Burger';
-- You have 'monster_fries.png', 'behemoth.png', 'kraken.png'.
-- Assign them to burgers if you wish:
-- UPDATE Menu_Items SET image_path = 'assets/images/menu/behemoth.png' WHERE name = 'Double Patty Burger';

-- Pizza
UPDATE Menu_Items SET image_path = 'assets/images/menu/meat.png' WHERE name = 'Meat Lovers Pizza';
-- You have 'margh.jpg' (Margherita). Maybe use for Pepperoni?
-- UPDATE Menu_Items SET image_path = 'assets/images/menu/margh.jpg' WHERE name = 'Pepperoni Pizza';
