package com.example.mycafeadminap.data

object Seeder {
    const val ADMIN_EMAIL = "admin@smartcafe.test"
    const val ADMIN_PASSWORD = "Admin123!"

    suspend fun seedIfEmpty(db: AppDatabase) {
        if (db.userDb().findByEmail(ADMIN_EMAIL) == null) {
            val salt = PasswordHasher.newSalt()
            db.userDb().insert(
                User(
                    email = ADMIN_EMAIL,
                    displayName = "Cafe Admin",
                    role = "admin",
                    passwordHash = PasswordHasher.hash(ADMIN_PASSWORD, salt),
                    salt = salt
                )
            )
        }
        if (db.MenuItemDb().count() == 0) {
            sampleMenu.forEach { db.MenuItemDb().Insert(it) }
        }

        // Simulated customer orders: 30 orders spread over the last 30 days (only if there are none yet)
        if (db.orderDb().countOrders() == 0) {
            OrderGenerator.generate(db, 30, 30)
        }
    }

    private val sampleMenu = listOf(
        MenuItem(name = "Espresso", category = "Drinks", description = "Single shot to kick start your day", price = 2.20),
        MenuItem(name = "Latte", category = "Drinks", description = "Espresso with steamed milk", price = 3.20),
        MenuItem(name = "Mocha", category = "Drinks", description = "Espresso with steamed milk and chocolate", price = 3.50),
        MenuItem(name = "Americano", category = "Drinks", description = "Espresso with hot water", price = 3.00),
        MenuItem(name = "Macchiato", category = "Drinks", description = "Espresso stained with a small amount of milk", price = 3.10),
        MenuItem(name = "Hot Chocolate", category = "Drinks", description = "Chocolate‑flavoured drink made by heating milk with cocoa powder", price = 1.90),
        MenuItem(name = "Matcha", category = "Drinks", description = "Green tea made from finely ground Japanese tea leaves whisked into water", price = 3.10),
        MenuItem(name = "Cappuccino", category = "Drinks", description = "Espresso, milk and foam", price = 3.10),
        MenuItem(name = "Croissant", category = "Pastries", description = "Butter croissant, mastered by our baker", price = 1.50),
        MenuItem(name = "Pain au chocolat", category = "Pastries", description = "Golden pastry with a rich dark chocolate filling.", price = 1.70),
        MenuItem(name = "Almond Croissant", category = "Pastries", description = "Butter croissant, topped with toasted almond flakes", price = 1.60),
        MenuItem(name = "Blueberry Muffin", category = "Pastries", description = "Buttermilk muffin filled with fresh blueberries", price = 2.80),
        MenuItem(name = "Cinnamon roll", category = "Pastries", description = "Sweet and sticky ", price = 1.90),
        MenuItem(name = "Breakfast Sandwich", category = "Breakfast", description = "Sausage patty,egg and cheese in a breakfast roll", price = 2.00),
        MenuItem(name = "Porridge", category = "Breakfast", description = "Oat porridge with semi skimmed milk added ", price = 1.20),
        MenuItem(name = "Hashbrown", category = "Breakfast", description = "Fried potatoes until golden brown", price = 1.00),
        MenuItem(name = "Cheese Sandwich", category = "Food", description = "Cheddar cheese with butter on soft white hovis bread", price = 2.50),
        MenuItem(name = "Ham & Cheddar Croissant", category = "Food", description = "Melted mozzarella and ham in a croissant", price = 1.30),
        MenuItem(name = "Sprinkle cake", category = "Desert", description = "Cake decorated with sprinkles for extra taste and visual appeal", price = 2.00),
        MenuItem(name = "Sponge cake", category = "Desert", description = "Sponge cake is a light, airy cake with sponge like texture", price = 2.40),
        MenuItem(name = "Red velvet cake", category = "Desert", description = "Chocolate flavoured cake with a soft, velvety crumb and its signature red color", price = 2.00)

    )
}
