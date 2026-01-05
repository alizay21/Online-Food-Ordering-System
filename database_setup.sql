-- MultiMeal Project Database Setup
-- Run this SQL script to set up the database with 2 restaurants per cuisine and 2 menu items per restaurant
-- IMPORTANT: This script uses 'stock_amount' to match the Java code

USE multimeal_db;

-- Ensure payment_method column exists in Orders table
ALTER TABLE Orders ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20) DEFAULT 'Cash';

-- Ensure size_at_order column exists in Order_Items table
ALTER TABLE Order_Items ADD COLUMN IF NOT EXISTS size_at_order VARCHAR(20) DEFAULT 'Medium';

-- ============================================
-- 1. CLEAN UP AND RESET
-- This ensures no duplicates and resets ID counters to 1
-- ============================================
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE Menu_Items;
TRUNCATE TABLE Restaurants;
TRUNCATE TABLE Cuisines;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 2. INSERT CUISINES (Using the correct order as requested)
-- cuisine_id: 1=Desi, 2=Italian, 3=Chinese, 4=Burger, 5=Pizza
-- ============================================
INSERT INTO Cuisines (cuisine_id, name) VALUES
(1, 'Desi'),
(2, 'Italian'),
(3, 'Chinese'),
(4, 'Burger'),
(5, 'Pizza');

-- ============================================
-- 3. INSERT RESTAURANTS (2 per cuisine = 10 total)
-- Forcing rest_id to match menu_items logic (1-10)
-- ============================================

-- Desi Cuisine (cuisine_id = 1) - 2 restaurants
INSERT INTO Restaurants (rest_id, name, cuisine_id, image_path) VALUES
(1, 'Karachi Biryani', 1, 'assets/images/restaurants/desi_karachi_biryani.jpg'),
(2, 'Lahore Grill', 1, 'assets/images/restaurants/desi_lahore_grill.jpg');

-- Italian Cuisine (cuisine_id = 2) - 2 restaurants
INSERT INTO Restaurants (rest_id, name, cuisine_id, image_path) VALUES
(3, 'Roma Pasta House', 2, 'assets/images/restaurants/italian_roma_pasta.jpg'),
(4, 'Napoli Trattoria', 2, 'assets/images/restaurants/italian_napoli_trattoria.jpg');

-- Chinese Cuisine (cuisine_id = 3) - 2 restaurants
INSERT INTO Restaurants (rest_id, name, cuisine_id, image_path) VALUES
(5, 'Dragon Wok', 3, 'assets/images/restaurants/chinese_dragon_wok.jpg'),
(6, 'Great Wall Dine', 3, 'assets/images/restaurants/chinese_great_wall.jpg');

-- Burger Cuisine (cuisine_id = 4) - 2 restaurants
INSERT INTO Restaurants (rest_id, name, cuisine_id, image_path) VALUES
(7, 'Smash Burger Bar', 4, 'assets/images/restaurants/burger_smash_bar.jpg'),
(8, 'Classic Burger Co', 4, 'assets/images/restaurants/burger_classic_co.jpg');

-- Pizza Cuisine (cuisine_id = 5) - 2 restaurants
INSERT INTO Restaurants (rest_id, name, cuisine_id, image_path) VALUES
(9, 'Cheesy Slice', 5, 'assets/images/restaurants/pizza_cheesy_slice.jpg'),
(10, 'New York Pie', 5, 'assets/images/restaurants/pizza_newyork_pie.jpg');

-- ============================================
-- 4. MENU ITEMS (2 items per restaurant, 3 sizes each = 6 variants per restaurant)
-- IMPORTANT: Using 'stock_amount' column to match Java code
-- rest_id mapping: 1-2=Desi, 3-4=Italian, 5-6=Chinese, 7-8=Burger, 9-10=Pizza
-- ============================================

-- KARACHI BIRYANI (rest_id = 1) - Desi
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(1, 1, 'Chicken Biryani', 'Small', 'Fragrant basmati rice with tender chicken pieces.', 350, 50, 'assets/images/menu/chicken_biryani.png', 20),
(2, 1, 'Chicken Biryani', 'Medium', 'Fragrant basmati rice with tender chicken pieces.', 500, 40, 'assets/images/menu/chicken_biryani.png', 20),
(3, 1, 'Chicken Biryani', 'Large', 'Fragrant basmati rice with tender chicken pieces.', 650, 30, 'assets/images/menu/chicken_biryani.png', 20),
(4, 1, 'Beef Pulao', 'Small', 'Aromatic rice cooked with beef and spices.', 320, 45, 'assets/images/menu/beef_pulao.png', 20),
(5, 1, 'Beef Pulao', 'Medium', 'Aromatic rice cooked with beef and spices.', 450, 35, 'assets/images/menu/beef_pulao.png', 20),
(6, 1, 'Beef Pulao', 'Large', 'Aromatic rice cooked with beef and spices.', 580, 25, 'assets/images/menu/beef_pulao.png', 20);

-- LAHORE GRILL (rest_id = 2) - Desi
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(7, 2, 'Chicken Karahi', 'Small', 'Traditional wok-cooked chicken with tomatoes.', 380, 50, 'assets/images/menu/chicken_karahi.png', 20),
(8, 2, 'Chicken Karahi', 'Medium', 'Traditional wok-cooked chicken with tomatoes.', 520, 40, 'assets/images/menu/chicken_karahi.png', 20),
(9, 2, 'Chicken Karahi', 'Large', 'Traditional wok-cooked chicken with tomatoes.', 680, 30, 'assets/images/menu/chicken_karahi.png', 20),
(10, 2, 'Seekh Kebab', 'Small', 'Grilled minced meat skewers.', 280, 60, 'assets/images/menu/seekh_kebab.png', 15),
(11, 2, 'Seekh Kebab', 'Medium', 'Grilled minced meat skewers.', 400, 50, 'assets/images/menu/seekh_kebab.png', 15),
(12, 2, 'Seekh Kebab', 'Large', 'Grilled minced meat skewers.', 520, 40, 'assets/images/menu/seekh_kebab.png', 15);

-- ROMA PASTA HOUSE (rest_id = 3) - Italian
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(13, 3, 'Spaghetti Carbonara', 'Small', 'Creamy pasta with bacon and parmesan.', 450, 50, 'assets/images/menu/carbonara.png', 15),
(14, 3, 'Spaghetti Carbonara', 'Medium', 'Creamy pasta with bacon and parmesan.', 620, 40, 'assets/images/menu/carbonara.png', 15),
(15, 3, 'Spaghetti Carbonara', 'Large', 'Creamy pasta with bacon and parmesan.', 800, 30, 'assets/images/menu/carbonara.png', 15),
(16, 3, 'Fettuccine Alfredo', 'Small', 'Rich creamy pasta with parmesan cheese.', 420, 45, 'assets/images/menu/alfredo.png', 15),
(17, 3, 'Fettuccine Alfredo', 'Medium', 'Rich creamy pasta with parmesan cheese.', 580, 35, 'assets/images/menu/alfredo.png', 15),
(18, 3, 'Fettuccine Alfredo', 'Large', 'Rich creamy pasta with parmesan cheese.', 750, 25, 'assets/images/menu/alfredo.png', 15);

-- NAPOLI TRATTORIA (rest_id = 4) - Italian
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(19, 4, 'Lasagna', 'Small', 'Layered pasta with meat and cheese.', 480, 40, 'assets/images/menu/lasagna.png', 20),
(20, 4, 'Lasagna', 'Medium', 'Layered pasta with meat and cheese.', 650, 30, 'assets/images/menu/lasagna.png', 20),
(21, 4, 'Lasagna', 'Large', 'Layered pasta with meat and cheese.', 850, 20, 'assets/images/menu/lasagna.png', 20),
(22, 4, 'Ravioli', 'Small', 'Stuffed pasta with ricotta and spinach.', 450, 45, 'assets/images/menu/ravioli.png', 15),
(23, 4, 'Ravioli', 'Medium', 'Stuffed pasta with ricotta and spinach.', 620, 35, 'assets/images/menu/ravioli.png', 15),
(24, 4, 'Ravioli', 'Large', 'Stuffed pasta with ricotta and spinach.', 800, 25, 'assets/images/menu/ravioli.png', 15);

-- DRAGON WOK (rest_id = 5) - Chinese
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(25, 5, 'Sweet and Sour Chicken', 'Small', 'Crispy chicken in tangy sweet and sour sauce.', 380, 60, 'assets/images/menu/sweet_sour_chicken.png', 15),
(26, 5, 'Sweet and Sour Chicken', 'Medium', 'Crispy chicken in tangy sweet and sour sauce.', 520, 50, 'assets/images/menu/sweet_sour_chicken.png', 15),
(27, 5, 'Sweet and Sour Chicken', 'Large', 'Crispy chicken in tangy sweet and sour sauce.', 680, 40, 'assets/images/menu/sweet_sour_chicken.png', 15),
(28, 5, 'Kung Pao Chicken', 'Small', 'Spicy stir-fried chicken with peanuts.', 400, 55, 'assets/images/menu/kung_pao.png', 15),
(29, 5, 'Kung Pao Chicken', 'Medium', 'Spicy stir-fried chicken with peanuts.', 550, 45, 'assets/images/menu/kung_pao.png', 15),
(30, 5, 'Kung Pao Chicken', 'Large', 'Spicy stir-fried chicken with peanuts.', 720, 35, 'assets/images/menu/kung_pao.png', 15);

-- GREAT WALL DINE (rest_id = 6) - Chinese
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(31, 6, 'General Tso Chicken', 'Small', 'Crispy chicken in spicy-sweet sauce.', 420, 50, 'assets/images/menu/general_tso.png', 15),
(32, 6, 'General Tso Chicken', 'Medium', 'Crispy chicken in spicy-sweet sauce.', 580, 40, 'assets/images/menu/general_tso.png', 15),
(33, 6, 'General Tso Chicken', 'Large', 'Crispy chicken in spicy-sweet sauce.', 750, 30, 'assets/images/menu/general_tso.png', 15),
(34, 6, 'Orange Chicken', 'Small', 'Battered chicken in orange-flavored sauce.', 400, 55, 'assets/images/menu/orange_chicken.png', 15),
(35, 6, 'Orange Chicken', 'Medium', 'Battered chicken in orange-flavored sauce.', 550, 45, 'assets/images/menu/orange_chicken.png', 15),
(36, 6, 'Orange Chicken', 'Large', 'Battered chicken in orange-flavored sauce.', 720, 35, 'assets/images/menu/orange_chicken.png', 15);

-- SMASH BURGER BAR (rest_id = 7) - Burger
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(37, 7, 'Classic Cheeseburger', 'Small', 'Beef patty with cheese, lettuce, and tomato.', 350, 70, 'assets/images/menu/classic_cheese.png', 15),
(38, 7, 'Classic Cheeseburger', 'Medium', 'Beef patty with cheese, lettuce, and tomato.', 480, 60, 'assets/images/menu/classic_cheese.png', 15),
(39, 7, 'Classic Cheeseburger', 'Large', 'Beef patty with cheese, lettuce, and tomato.', 620, 50, 'assets/images/menu/classic_cheese.png', 15),
(40, 7, 'BBQ Bacon Burger', 'Small', 'Beef patty with BBQ sauce, bacon, and onion rings.', 420, 60, 'assets/images/menu/bbq_strip.png', 15),
(41, 7, 'BBQ Bacon Burger', 'Medium', 'Beef patty with BBQ sauce, bacon, and onion rings.', 580, 50, 'assets/images/menu/bbq_strip.png', 15),
(42, 7, 'BBQ Bacon Burger', 'Large', 'Beef patty with BBQ sauce, bacon, and onion rings.', 750, 40, 'assets/images/menu/bbq_strip.png', 15);

-- CLASSIC BURGER CO (rest_id = 8) - Burger
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(43, 8, 'Double Patty Burger', 'Small', 'Two beef patties with double cheese.', 450, 55, 'assets/images/menu/double_patty.png', 15),
(44, 8, 'Double Patty Burger', 'Medium', 'Two beef patties with double cheese.', 620, 45, 'assets/images/menu/double_patty.png', 15),
(45, 8, 'Double Patty Burger', 'Large', 'Two beef patties with double cheese.', 800, 35, 'assets/images/menu/double_patty.png', 15),
(46, 8, 'Mushroom Swiss Burger', 'Small', 'Beef patty with sautéed mushrooms and Swiss cheese.', 400, 50, 'assets/images/menu/mushroom_swiss.png', 15),
(47, 8, 'Mushroom Swiss Burger', 'Medium', 'Beef patty with sautéed mushrooms and Swiss cheese.', 550, 40, 'assets/images/menu/mushroom_swiss.png', 15),
(48, 8, 'Mushroom Swiss Burger', 'Large', 'Beef patty with sautéed mushrooms and Swiss cheese.', 720, 30, 'assets/images/menu/mushroom_swiss.png', 15);

-- CHEESY SLICE (rest_id = 9) - Pizza
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(49, 9, 'Pepperoni Pizza', 'Small', 'Classic pepperoni with mozzarella cheese.', 450, 60, 'assets/images/menu/pepperoni.png', 20),
(50, 9, 'Pepperoni Pizza', 'Medium', 'Classic pepperoni with mozzarella cheese.', 620, 50, 'assets/images/menu/pepperoni.png', 20),
(51, 9, 'Pepperoni Pizza', 'Large', 'Classic pepperoni with mozzarella cheese.', 800, 40, 'assets/images/menu/pepperoni.png', 20),
(52, 9, 'BBQ Chicken Pizza', 'Small', 'BBQ sauce, chicken, onions, and cheese.', 480, 55, 'assets/images/menu/bbq.png', 20),
(53, 9, 'BBQ Chicken Pizza', 'Medium', 'BBQ sauce, chicken, onions, and cheese.', 650, 45, 'assets/images/menu/bbq.png', 20),
(54, 9, 'BBQ Chicken Pizza', 'Large', 'BBQ sauce, chicken, onions, and cheese.', 850, 35, 'assets/images/menu/bbq.png', 20);

-- NEW YORK PIE (rest_id = 10) - Pizza
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock_amount, image_path, packaging_fee) VALUES
(55, 10, 'Supreme Pizza', 'Small', 'Pepperoni, sausage, peppers, mushrooms, and olives.', 500, 50, 'assets/images/menu/supreme.png', 20),
(56, 10, 'Supreme Pizza', 'Medium', 'Pepperoni, sausage, peppers, mushrooms, and olives.', 680, 40, 'assets/images/menu/supreme.png', 20),
(57, 10, 'Supreme Pizza', 'Large', 'Pepperoni, sausage, peppers, mushrooms, and olives.', 900, 30, 'assets/images/menu/supreme.png', 20),
(58, 10, 'Meat Lovers Pizza', 'Small', 'Pepperoni, sausage, ham, and bacon.', 520, 45, 'assets/images/menu/meat.png', 20),
(59, 10, 'Meat Lovers Pizza', 'Medium', 'Pepperoni, sausage, ham, and bacon.', 700, 35, 'assets/images/menu/meat.png', 20),
(60, 10, 'Meat Lovers Pizza', 'Large', 'Pepperoni, sausage, ham, and bacon.', 920, 25, 'assets/images/menu/meat.png', 20);
