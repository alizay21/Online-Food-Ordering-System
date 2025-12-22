-- MultiMeal Project Database Setup
-- Run this SQL script to set up the database with 3 restaurants per cuisine and 3 menu items per restaurant

USE multimeal_db;

-- Ensure payment_method column exists in Orders table
ALTER TABLE Orders ADD COLUMN IF NOT EXISTS payment_method VARCHAR(20) DEFAULT 'Cash';

-- Ensure size_at_order column exists in Order_Items table
ALTER TABLE Order_Items ADD COLUMN IF NOT EXISTS size_at_order VARCHAR(20) DEFAULT 'Medium';

-- ============================================
-- RESTAURANTS (3 per cuisine = 15 total)
-- ============================================

-- Desi Cuisine (3 restaurants)
INSERT INTO Restaurants (name, cuisine_id, image_path) VALUES
('Karachi Biryani', 1, 'assets/images/restaurants/desi_karachi_biryani.jpg'),
('Lahore Grill', 1, 'assets/images/restaurants/desi_lahore_grill.jpg'),
('Pindi Dhaba', 1, 'assets/images/restaurants/desi_pindi_dhaba.jpg')
ON DUPLICATE KEY UPDATE name=name;

-- Italian Cuisine (3 restaurants)
INSERT INTO Restaurants (name, cuisine_id, image_path) VALUES
('Roma Pasta House', 2, 'assets/images/restaurants/italian_roma_pasta.jpg'),
('Napoli Trattoria', 2, 'assets/images/restaurants/italian_napoli_trattoria.jpg'),
('Milano Bistro', 2, 'assets/images/restaurants/italian_milano_bistro.jpg')
ON DUPLICATE KEY UPDATE name=name;

-- Pizza Cuisine (3 restaurants)
INSERT INTO Restaurants (name, cuisine_id, image_path) VALUES
('Cheesy Slice', 3, 'assets/images/restaurants/pizza_cheesy_slice.jpg'),
('New York Pie', 3, 'assets/images/restaurants/pizza_newyork_pie.jpg'),
('Stone Oven Pizza', 3, 'assets/images/restaurants/pizza_stone_oven.jpg')
ON DUPLICATE KEY UPDATE name=name;

-- Burger Cuisine (3 restaurants)
INSERT INTO Restaurants (name, cuisine_id, image_path) VALUES
('Smash Burger Bar', 4, 'assets/images/restaurants/burger_smash_bar.jpg'),
('Classic Burger Co', 4, 'assets/images/restaurants/burger_classic_co.jpg'),
('Grill & Bun', 4, 'assets/images/restaurants/burger_grill_bun.jpg')
ON DUPLICATE KEY UPDATE name=name;

-- Chinese Cuisine (3 restaurants)
INSERT INTO Restaurants (name, cuisine_id, image_path) VALUES
('Dragon Wok', 5, 'assets/images/restaurants/chinese_dragon_wok.jpg'),
('Great Wall Dine', 5, 'assets/images/restaurants/chinese_great_wall.jpg'),
('Panda Kitchen', 5, 'assets/images/restaurants/chinese_panda_kitchen.jpg')
ON DUPLICATE KEY UPDATE name=name;

-- ============================================
-- MENU ITEMS (3 items per restaurant, 3 sizes each = 9 variants per restaurant)
-- ============================================

-- Note: item_id is auto-incremented. Adjust rest_id based on your actual restaurant IDs.
-- Assuming rest_id 1-3 are Desi, 4-6 are Italian, 7-9 are Pizza, 10-12 are Burger, 13-15 are Chinese

-- KARACHI BIRYANI (rest_id = 1) - Desi
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(1, 1, 'Chicken Biryani', 'Small', 'Fragrant basmati rice with tender chicken pieces.', 350, 50, 'assets/images/menu/chicken_biryani_small.jpg', 20),
(2, 1, 'Chicken Biryani', 'Medium', 'Fragrant basmati rice with tender chicken pieces.', 500, 40, 'assets/images/menu/chicken_biryani_medium.jpg', 20),
(3, 1, 'Chicken Biryani', 'Large', 'Fragrant basmati rice with tender chicken pieces.', 650, 30, 'assets/images/menu/chicken_biryani_large.jpg', 20),
(4, 1, 'Beef Pulao', 'Small', 'Aromatic rice cooked with beef and spices.', 320, 45, 'assets/images/menu/beef_pulao_small.jpg', 20),
(5, 1, 'Beef Pulao', 'Medium', 'Aromatic rice cooked with beef and spices.', 450, 35, 'assets/images/menu/beef_pulao_medium.jpg', 20),
(6, 1, 'Beef Pulao', 'Large', 'Aromatic rice cooked with beef and spices.', 580, 25, 'assets/images/menu/beef_pulao_large.jpg', 20),
(7, 1, 'Mutton Biryani', 'Small', 'Rich biryani with tender mutton pieces.', 400, 40, 'assets/images/menu/mutton_biryani_small.jpg', 20),
(8, 1, 'Mutton Biryani', 'Medium', 'Rich biryani with tender mutton pieces.', 550, 30, 'assets/images/menu/mutton_biryani_medium.jpg', 20),
(9, 1, 'Mutton Biryani', 'Large', 'Rich biryani with tender mutton pieces.', 700, 20, 'assets/images/menu/mutton_biryani_large.jpg', 20);

-- LAHORE GRILL (rest_id = 2) - Desi
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(10, 2, 'Chicken Karahi', 'Small', 'Traditional wok-cooked chicken with tomatoes.', 380, 50, 'assets/images/menu/chicken_karahi_small.jpg', 20),
(11, 2, 'Chicken Karahi', 'Medium', 'Traditional wok-cooked chicken with tomatoes.', 520, 40, 'assets/images/menu/chicken_karahi_medium.jpg', 20),
(12, 2, 'Chicken Karahi', 'Large', 'Traditional wok-cooked chicken with tomatoes.', 680, 30, 'assets/images/menu/chicken_karahi_large.jpg', 20),
(13, 2, 'Seekh Kebab', 'Small', 'Grilled minced meat skewers.', 280, 60, 'assets/images/menu/seekh_kebab_small.jpg', 15),
(14, 2, 'Seekh Kebab', 'Medium', 'Grilled minced meat skewers.', 400, 50, 'assets/images/menu/seekh_kebab_medium.jpg', 15),
(15, 2, 'Seekh Kebab', 'Large', 'Grilled minced meat skewers.', 520, 40, 'assets/images/menu/seekh_kebab_large.jpg', 15),
(16, 2, 'Butter Chicken', 'Small', 'Creamy tomato-based curry with tender chicken.', 360, 45, 'assets/images/menu/butter_chicken_small.jpg', 20),
(17, 2, 'Butter Chicken', 'Medium', 'Creamy tomato-based curry with tender chicken.', 500, 35, 'assets/images/menu/butter_chicken_medium.jpg', 20),
(18, 2, 'Butter Chicken', 'Large', 'Creamy tomato-based curry with tender chicken.', 640, 25, 'assets/images/menu/butter_chicken_large.jpg', 20);

-- PINDI DHABA (rest_id = 3) - Desi
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(19, 3, 'Nihari', 'Small', 'Slow-cooked beef stew with aromatic spices.', 420, 40, 'assets/images/menu/nihari_small.jpg', 20),
(20, 3, 'Nihari', 'Medium', 'Slow-cooked beef stew with aromatic spices.', 580, 30, 'assets/images/menu/nihari_medium.jpg', 20),
(21, 3, 'Nihari', 'Large', 'Slow-cooked beef stew with aromatic spices.', 750, 20, 'assets/images/menu/nihari_large.jpg', 20),
(22, 3, 'Haleem', 'Small', 'Thick lentil and meat stew.', 350, 50, 'assets/images/menu/haleem_small.jpg', 20),
(23, 3, 'Haleem', 'Medium', 'Thick lentil and meat stew.', 480, 40, 'assets/images/menu/haleem_medium.jpg', 20),
(24, 3, 'Haleem', 'Large', 'Thick lentil and meat stew.', 620, 30, 'assets/images/menu/haleem_large.jpg', 20),
(25, 3, 'Chapli Kebab', 'Small', 'Spiced minced meat patties.', 300, 55, 'assets/images/menu/chapli_kebab_small.jpg', 15),
(26, 3, 'Chapli Kebab', 'Medium', 'Spiced minced meat patties.', 420, 45, 'assets/images/menu/chapli_kebab_medium.jpg', 15),
(27, 3, 'Chapli Kebab', 'Large', 'Spiced minced meat patties.', 540, 35, 'assets/images/menu/chapli_kebab_large.jpg', 15);

-- ROMA PASTA HOUSE (rest_id = 4) - Italian
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(28, 4, 'Spaghetti Carbonara', 'Small', 'Creamy pasta with bacon and parmesan.', 450, 50, 'assets/images/menu/carbonara_small.jpg', 15),
(29, 4, 'Spaghetti Carbonara', 'Medium', 'Creamy pasta with bacon and parmesan.', 620, 40, 'assets/images/menu/carbonara_medium.jpg', 15),
(30, 4, 'Spaghetti Carbonara', 'Large', 'Creamy pasta with bacon and parmesan.', 800, 30, 'assets/images/menu/carbonara_large.jpg', 15),
(31, 4, 'Fettuccine Alfredo', 'Small', 'Rich creamy pasta with parmesan cheese.', 420, 45, 'assets/images/menu/alfredo_small.jpg', 15),
(32, 4, 'Fettuccine Alfredo', 'Medium', 'Rich creamy pasta with parmesan cheese.', 580, 35, 'assets/images/menu/alfredo_medium.jpg', 15),
(33, 4, 'Fettuccine Alfredo', 'Large', 'Rich creamy pasta with parmesan cheese.', 750, 25, 'assets/images/menu/alfredo_large.jpg', 15),
(34, 4, 'Penne Arrabbiata', 'Small', 'Spicy tomato pasta with garlic.', 380, 50, 'assets/images/menu/arrabbiata_small.jpg', 15),
(35, 4, 'Penne Arrabbiata', 'Medium', 'Spicy tomato pasta with garlic.', 520, 40, 'assets/images/menu/arrabbiata_medium.jpg', 15),
(36, 4, 'Penne Arrabbiata', 'Large', 'Spicy tomato pasta with garlic.', 680, 30, 'assets/images/menu/arrabbiata_large.jpg', 15);

-- NAPOLI TRATTORIA (rest_id = 5) - Italian
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(37, 5, 'Lasagna', 'Small', 'Layered pasta with meat and cheese.', 480, 40, 'assets/images/menu/lasagna_small.jpg', 20),
(38, 5, 'Lasagna', 'Medium', 'Layered pasta with meat and cheese.', 650, 30, 'assets/images/menu/lasagna_medium.jpg', 20),
(39, 5, 'Lasagna', 'Large', 'Layered pasta with meat and cheese.', 850, 20, 'assets/images/menu/lasagna_large.jpg', 20),
(40, 5, 'Ravioli', 'Small', 'Stuffed pasta with ricotta and spinach.', 450, 45, 'assets/images/menu/ravioli_small.jpg', 15),
(41, 5, 'Ravioli', 'Medium', 'Stuffed pasta with ricotta and spinach.', 620, 35, 'assets/images/menu/ravioli_medium.jpg', 15),
(42, 5, 'Ravioli', 'Large', 'Stuffed pasta with ricotta and spinach.', 800, 25, 'assets/images/menu/ravioli_large.jpg', 15),
(43, 5, 'Risotto', 'Small', 'Creamy Italian rice dish.', 420, 40, 'assets/images/menu/risotto_small.jpg', 15),
(44, 5, 'Risotto', 'Medium', 'Creamy Italian rice dish.', 580, 30, 'assets/images/menu/risotto_medium.jpg', 15),
(45, 5, 'Risotto', 'Large', 'Creamy Italian rice dish.', 750, 20, 'assets/images/menu/risotto_large.jpg', 15);

-- MILANO BISTRO (rest_id = 6) - Italian
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(46, 6, 'Margherita Pizza', 'Small', 'Classic pizza with tomato, mozzarella, and basil.', 400, 60, 'assets/images/menu/margherita_small.jpg', 20),
(47, 6, 'Margherita Pizza', 'Medium', 'Classic pizza with tomato, mozzarella, and basil.', 550, 50, 'assets/images/menu/margherita_medium.jpg', 20),
(48, 6, 'Margherita Pizza', 'Large', 'Classic pizza with tomato, mozzarella, and basil.', 720, 40, 'assets/images/menu/margherita_large.jpg', 20),
(49, 6, 'Chicken Pasta', 'Small', 'Pasta with grilled chicken and marinara sauce.', 450, 50, 'assets/images/menu/chicken_pasta_small.jpg', 15),
(50, 6, 'Chicken Pasta', 'Medium', 'Pasta with grilled chicken and marinara sauce.', 620, 40, 'assets/images/menu/chicken_pasta_medium.jpg', 15),
(51, 6, 'Chicken Pasta', 'Large', 'Pasta with grilled chicken and marinara sauce.', 800, 30, 'assets/images/menu/chicken_pasta_large.jpg', 15),
(52, 6, 'Bruschetta', 'Small', 'Toasted bread with tomatoes and garlic.', 280, 70, 'assets/images/menu/bruschetta_small.jpg', 10),
(53, 6, 'Bruschetta', 'Medium', 'Toasted bread with tomatoes and garlic.', 380, 60, 'assets/images/menu/bruschetta_medium.jpg', 10),
(54, 6, 'Bruschetta', 'Large', 'Toasted bread with tomatoes and garlic.', 480, 50, 'assets/images/menu/bruschetta_large.jpg', 10);

-- CHEESY SLICE (rest_id = 7) - Pizza
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(55, 7, 'Pepperoni Pizza', 'Small', 'Classic pepperoni with mozzarella cheese.', 450, 60, 'assets/images/menu/pepperoni_small.jpg', 20),
(56, 7, 'Pepperoni Pizza', 'Medium', 'Classic pepperoni with mozzarella cheese.', 620, 50, 'assets/images/menu/pepperoni_medium.jpg', 20),
(57, 7, 'Pepperoni Pizza', 'Large', 'Classic pepperoni with mozzarella cheese.', 800, 40, 'assets/images/menu/pepperoni_large.jpg', 20),
(58, 7, 'BBQ Chicken Pizza', 'Small', 'BBQ sauce, chicken, onions, and cheese.', 480, 55, 'assets/images/menu/bbq_chicken_small.jpg', 20),
(59, 7, 'BBQ Chicken Pizza', 'Medium', 'BBQ sauce, chicken, onions, and cheese.', 650, 45, 'assets/images/menu/bbq_chicken_medium.jpg', 20),
(60, 7, 'BBQ Chicken Pizza', 'Large', 'BBQ sauce, chicken, onions, and cheese.', 850, 35, 'assets/images/menu/bbq_chicken_large.jpg', 20),
(61, 7, 'Hawaiian Pizza', 'Small', 'Ham, pineapple, and mozzarella.', 420, 50, 'assets/images/menu/hawaiian_small.jpg', 20),
(62, 7, 'Hawaiian Pizza', 'Medium', 'Ham, pineapple, and mozzarella.', 580, 40, 'assets/images/menu/hawaiian_medium.jpg', 20),
(63, 7, 'Hawaiian Pizza', 'Large', 'Ham, pineapple, and mozzarella.', 750, 30, 'assets/images/menu/hawaiian_large.jpg', 20);

-- NEW YORK PIE (rest_id = 8) - Pizza
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(64, 8, 'Supreme Pizza', 'Small', 'Pepperoni, sausage, peppers, mushrooms, and olives.', 500, 50, 'assets/images/menu/supreme_small.jpg', 20),
(65, 8, 'Supreme Pizza', 'Medium', 'Pepperoni, sausage, peppers, mushrooms, and olives.', 680, 40, 'assets/images/menu/supreme_medium.jpg', 20),
(66, 8, 'Supreme Pizza', 'Large', 'Pepperoni, sausage, peppers, mushrooms, and olives.', 900, 30, 'assets/images/menu/supreme_large.jpg', 20),
(67, 8, 'Meat Lovers Pizza', 'Small', 'Pepperoni, sausage, ham, and bacon.', 520, 45, 'assets/images/menu/meat_lovers_small.jpg', 20),
(68, 8, 'Meat Lovers Pizza', 'Medium', 'Pepperoni, sausage, ham, and bacon.', 700, 35, 'assets/images/menu/meat_lovers_medium.jpg', 20),
(69, 8, 'Meat Lovers Pizza', 'Large', 'Pepperoni, sausage, ham, and bacon.', 920, 25, 'assets/images/menu/meat_lovers_large.jpg', 20),
(70, 8, 'Veggie Delight Pizza', 'Small', 'Bell peppers, mushrooms, onions, and olives.', 400, 55, 'assets/images/menu/veggie_delight_small.jpg', 20),
(71, 8, 'Veggie Delight Pizza', 'Medium', 'Bell peppers, mushrooms, onions, and olives.', 550, 45, 'assets/images/menu/veggie_delight_medium.jpg', 20),
(72, 8, 'Veggie Delight Pizza', 'Large', 'Bell peppers, mushrooms, onions, and olives.', 720, 35, 'assets/images/menu/veggie_delight_large.jpg', 20);

-- STONE OVEN PIZZA (rest_id = 9) - Pizza
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(73, 9, 'Margherita Classic', 'Small', 'Fresh tomatoes, mozzarella, and basil.', 380, 60, 'assets/images/menu/margherita_classic_small.jpg', 20),
(74, 9, 'Margherita Classic', 'Medium', 'Fresh tomatoes, mozzarella, and basil.', 520, 50, 'assets/images/menu/margherita_classic_medium.jpg', 20),
(75, 9, 'Margherita Classic', 'Large', 'Fresh tomatoes, mozzarella, and basil.', 680, 40, 'assets/images/menu/margherita_classic_large.jpg', 20),
(76, 9, 'Four Cheese Pizza', 'Small', 'Mozzarella, cheddar, parmesan, and gorgonzola.', 450, 50, 'assets/images/menu/four_cheese_small.jpg', 20),
(77, 9, 'Four Cheese Pizza', 'Medium', 'Mozzarella, cheddar, parmesan, and gorgonzola.', 620, 40, 'assets/images/menu/four_cheese_medium.jpg', 20),
(78, 9, 'Four Cheese Pizza', 'Large', 'Mozzarella, cheddar, parmesan, and gorgonzola.', 800, 30, 'assets/images/menu/four_cheese_large.jpg', 20),
(79, 9, 'Chicken Tikka Pizza', 'Small', 'Spiced chicken tikka with onions and peppers.', 480, 45, 'assets/images/menu/chicken_tikka_pizza_small.jpg', 20),
(80, 9, 'Chicken Tikka Pizza', 'Medium', 'Spiced chicken tikka with onions and peppers.', 650, 35, 'assets/images/menu/chicken_tikka_pizza_medium.jpg', 20),
(81, 9, 'Chicken Tikka Pizza', 'Large', 'Spiced chicken tikka with onions and peppers.', 850, 25, 'assets/images/menu/chicken_tikka_pizza_large.jpg', 20);

-- SMASH BURGER BAR (rest_id = 10) - Burger
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(82, 10, 'Classic Cheeseburger', 'Small', 'Beef patty with cheese, lettuce, and tomato.', 350, 70, 'assets/images/menu/classic_cheese_small.jpg', 15),
(83, 10, 'Classic Cheeseburger', 'Medium', 'Beef patty with cheese, lettuce, and tomato.', 480, 60, 'assets/images/menu/classic_cheese_medium.jpg', 15),
(84, 10, 'Classic Cheeseburger', 'Large', 'Beef patty with cheese, lettuce, and tomato.', 620, 50, 'assets/images/menu/classic_cheese_large.jpg', 15),
(85, 10, 'BBQ Bacon Burger', 'Small', 'Beef patty with BBQ sauce, bacon, and onion rings.', 420, 60, 'assets/images/menu/bbq_bacon_small.jpg', 15),
(86, 10, 'BBQ Bacon Burger', 'Medium', 'Beef patty with BBQ sauce, bacon, and onion rings.', 580, 50, 'assets/images/menu/bbq_bacon_medium.jpg', 15),
(87, 10, 'BBQ Bacon Burger', 'Large', 'Beef patty with BBQ sauce, bacon, and onion rings.', 750, 40, 'assets/images/menu/bbq_bacon_large.jpg', 15),
(88, 10, 'Chicken Burger', 'Small', 'Grilled chicken breast with mayo and veggies.', 380, 65, 'assets/images/menu/chicken_burger_small.jpg', 15),
(89, 10, 'Chicken Burger', 'Medium', 'Grilled chicken breast with mayo and veggies.', 520, 55, 'assets/images/menu/chicken_burger_medium.jpg', 15),
(90, 10, 'Chicken Burger', 'Large', 'Grilled chicken breast with mayo and veggies.', 680, 45, 'assets/images/menu/chicken_burger_large.jpg', 15);

-- CLASSIC BURGER CO (rest_id = 11) - Burger
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(91, 11, 'Double Patty Burger', 'Small', 'Two beef patties with double cheese.', 450, 55, 'assets/images/menu/double_patty_small.jpg', 15),
(92, 11, 'Double Patty Burger', 'Medium', 'Two beef patties with double cheese.', 620, 45, 'assets/images/menu/double_patty_medium.jpg', 15),
(93, 11, 'Double Patty Burger', 'Large', 'Two beef patties with double cheese.', 800, 35, 'assets/images/menu/double_patty_large.jpg', 15),
(94, 11, 'Mushroom Swiss Burger', 'Small', 'Beef patty with sautéed mushrooms and Swiss cheese.', 400, 50, 'assets/images/menu/mushroom_swiss_small.jpg', 15),
(95, 11, 'Mushroom Swiss Burger', 'Medium', 'Beef patty with sautéed mushrooms and Swiss cheese.', 550, 40, 'assets/images/menu/mushroom_swiss_medium.jpg', 15),
(96, 11, 'Mushroom Swiss Burger', 'Large', 'Beef patty with sautéed mushrooms and Swiss cheese.', 720, 30, 'assets/images/menu/mushroom_swiss_large.jpg', 15),
(97, 11, 'Veggie Burger', 'Small', 'Plant-based patty with fresh vegetables.', 350, 60, 'assets/images/menu/veggie_burger_small.jpg', 15),
(98, 11, 'Veggie Burger', 'Medium', 'Plant-based patty with fresh vegetables.', 480, 50, 'assets/images/menu/veggie_burger_medium.jpg', 15),
(99, 11, 'Veggie Burger', 'Large', 'Plant-based patty with fresh vegetables.', 620, 40, 'assets/images/menu/veggie_burger_large.jpg', 15);

-- GRILL & BUN (rest_id = 12) - Burger
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(100, 12, 'Jalapeño Burger', 'Small', 'Spicy beef patty with jalapeños and pepper jack cheese.', 420, 50, 'assets/images/menu/jalapeno_burger_small.jpg', 15),
(101, 12, 'Jalapeño Burger', 'Medium', 'Spicy beef patty with jalapeños and pepper jack cheese.', 580, 40, 'assets/images/menu/jalapeno_burger_medium.jpg', 15),
(102, 12, 'Jalapeño Burger', 'Large', 'Spicy beef patty with jalapeños and pepper jack cheese.', 750, 30, 'assets/images/menu/jalapeno_burger_large.jpg', 15),
(103, 12, 'Turkey Burger', 'Small', 'Lean turkey patty with cranberry sauce.', 380, 45, 'assets/images/menu/turkey_burger_small.jpg', 15),
(104, 12, 'Turkey Burger', 'Medium', 'Lean turkey patty with cranberry sauce.', 520, 35, 'assets/images/menu/turkey_burger_medium.jpg', 15),
(105, 12, 'Turkey Burger', 'Large', 'Lean turkey patty with cranberry sauce.', 680, 25, 'assets/images/menu/turkey_burger_large.jpg', 15),
(106, 12, 'Fish Burger', 'Small', 'Crispy fish fillet with tartar sauce.', 400, 40, 'assets/images/menu/fish_burger_small.jpg', 15),
(107, 12, 'Fish Burger', 'Medium', 'Crispy fish fillet with tartar sauce.', 550, 30, 'assets/images/menu/fish_burger_medium.jpg', 15),
(108, 12, 'Fish Burger', 'Large', 'Crispy fish fillet with tartar sauce.', 720, 20, 'assets/images/menu/fish_burger_large.jpg', 15);

-- DRAGON WOK (rest_id = 13) - Chinese
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(109, 13, 'Sweet and Sour Chicken', 'Small', 'Crispy chicken in tangy sweet and sour sauce.', 380, 60, 'assets/images/menu/sweet_sour_chicken_small.jpg', 15),
(110, 13, 'Sweet and Sour Chicken', 'Medium', 'Crispy chicken in tangy sweet and sour sauce.', 520, 50, 'assets/images/menu/sweet_sour_chicken_medium.jpg', 15),
(111, 13, 'Sweet and Sour Chicken', 'Large', 'Crispy chicken in tangy sweet and sour sauce.', 680, 40, 'assets/images/menu/sweet_sour_chicken_large.jpg', 15),
(112, 13, 'Kung Pao Chicken', 'Small', 'Spicy stir-fried chicken with peanuts.', 400, 55, 'assets/images/menu/kung_pao_small.jpg', 15),
(113, 13, 'Kung Pao Chicken', 'Medium', 'Spicy stir-fried chicken with peanuts.', 550, 45, 'assets/images/menu/kung_pao_medium.jpg', 15),
(114, 13, 'Kung Pao Chicken', 'Large', 'Spicy stir-fried chicken with peanuts.', 720, 35, 'assets/images/menu/kung_pao_large.jpg', 15),
(115, 13, 'Fried Rice', 'Small', 'Classic Chinese fried rice with vegetables.', 320, 70, 'assets/images/menu/fried_rice_small.jpg', 15),
(116, 13, 'Fried Rice', 'Medium', 'Classic Chinese fried rice with vegetables.', 450, 60, 'assets/images/menu/fried_rice_medium.jpg', 15),
(117, 13, 'Fried Rice', 'Large', 'Classic Chinese fried rice with vegetables.', 580, 50, 'assets/images/menu/fried_rice_large.jpg', 15);

-- GREAT WALL DINE (rest_id = 14) - Chinese
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(118, 14, 'General Tso Chicken', 'Small', 'Crispy chicken in spicy-sweet sauce.', 420, 50, 'assets/images/menu/general_tso_small.jpg', 15),
(119, 14, 'General Tso Chicken', 'Medium', 'Crispy chicken in spicy-sweet sauce.', 580, 40, 'assets/images/menu/general_tso_medium.jpg', 15),
(120, 14, 'General Tso Chicken', 'Large', 'Crispy chicken in spicy-sweet sauce.', 750, 30, 'assets/images/menu/general_tso_large.jpg', 15),
(121, 14, 'Orange Chicken', 'Small', 'Battered chicken in orange-flavored sauce.', 400, 55, 'assets/images/menu/orange_chicken_small.jpg', 15),
(122, 14, 'Orange Chicken', 'Medium', 'Battered chicken in orange-flavored sauce.', 550, 45, 'assets/images/menu/orange_chicken_medium.jpg', 15),
(123, 14, 'Orange Chicken', 'Large', 'Battered chicken in orange-flavored sauce.', 720, 35, 'assets/images/menu/orange_chicken_large.jpg', 15),
(124, 14, 'Chow Mein', 'Small', 'Stir-fried noodles with vegetables.', 350, 60, 'assets/images/menu/chow_mein_small.jpg', 15),
(125, 14, 'Chow Mein', 'Medium', 'Stir-fried noodles with vegetables.', 480, 50, 'assets/images/menu/chow_mein_medium.jpg', 15),
(126, 14, 'Chow Mein', 'Large', 'Stir-fried noodles with vegetables.', 620, 40, 'assets/images/menu/chow_mein_large.jpg', 15);

-- PANDA KITCHEN (rest_id = 15) - Chinese
INSERT INTO Menu_Items (item_id, rest_id, name, size, description, price, stock, image_path, packaging_fee) VALUES
(127, 15, 'Mapo Tofu', 'Small', 'Spicy Sichuan tofu with ground pork.', 360, 50, 'assets/images/menu/mapo_tofu_small.jpg', 15),
(128, 15, 'Mapo Tofu', 'Medium', 'Spicy Sichuan tofu with ground pork.', 500, 40, 'assets/images/menu/mapo_tofu_medium.jpg', 15),
(129, 15, 'Mapo Tofu', 'Large', 'Spicy Sichuan tofu with ground pork.', 650, 30, 'assets/images/menu/mapo_tofu_large.jpg', 15),
(130, 15, 'Beef Lo Mein', 'Small', 'Soft noodles with beef and vegetables.', 400, 45, 'assets/images/menu/beef_lo_mein_small.jpg', 15),
(131, 15, 'Beef Lo Mein', 'Medium', 'Soft noodles with beef and vegetables.', 550, 35, 'assets/images/menu/beef_lo_mein_medium.jpg', 15),
(132, 15, 'Beef Lo Mein', 'Large', 'Soft noodles with beef and vegetables.', 720, 25, 'assets/images/menu/beef_lo_mein_large.jpg', 15),
(133, 15, 'Spring Rolls', 'Small', 'Crispy vegetable spring rolls.', 280, 80, 'assets/images/menu/spring_rolls_small.jpg', 10),
(134, 15, 'Spring Rolls', 'Medium', 'Crispy vegetable spring rolls.', 380, 70, 'assets/images/menu/spring_rolls_medium.jpg', 10),
(135, 15, 'Spring Rolls', 'Large', 'Crispy vegetable spring rolls.', 480, 60, 'assets/images/menu/spring_rolls_large.jpg', 10);

