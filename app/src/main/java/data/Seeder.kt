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
        MenuItem(name = "Cappuccino", category = "Drinks", description = "Espresso, milk and foam", price = 3.10),
        MenuItem(name = "Hot Chocolate", category = "Drinks", description = "Made with whole milk", price = 1.90),
        MenuItem(name = "Croissant", category = "Pastries", description = "Butter croissant, mastered by our baker", price = 1.50),
        MenuItem(name = "Blueberry Muffin", category = "Pastries", description = "Buttermilk muffin filled with fresh blueberries", price = 2.80),
        MenuItem(name = "Cinnamon roll", category = "Pastries", description = "Sweet and sticky ", price = 1.90),
        MenuItem(name = "Cheese Sandwich", category = "Food", description = "Cheddar cheese with butter on soft white hovis bread", price = 2.50)
    )
}
