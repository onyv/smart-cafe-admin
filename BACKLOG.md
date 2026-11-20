# Backlog

## Target 1: 5 Nov (core features, database, first client review)
- [/] Create GitHub repo and push the first commit
- [/] Android project set up (Kotlin, Compose), package structure for MVVM
- [/] Room database set up with users, menu_items tables
- [/] FR-01 Admin login with email and password (salted password hashing)
- [ ] FR-02 Only admin accounts can reach admin screens
- [ ] FR-03 Sign out clears the session
- [/] FR-04 Menu list grouped by category
- [/] FR-05 Add menu item
- [/] FR-06 Edit menu item
- [/] FR-07 Delete menu item (with confirmation)
- [/] FR-08 Mark item available/unavailable
- [ ] FR-09 Input validation on menu forms
- [ ] SIM-01 Seed realistic menu (20+ items)
- [ ] Record client feedback in `CLIENT_MEETINGS.md`

## Target 2: 12 Nov (orders, feedback, analytics, second client review)
- [ ] Room tables for orders, order_items, feedback, notifications
- [/] SIM-02 Order generator (random orders, plus 30 days of history)
- [ ] SIM-03 A few customer accounts with previous orders
- [/] FR-11 Incoming orders list, newest first
- [/] FR-12 Order detail with item quantities and status
- [/] FR-13 Update order status (ordered, preparing, completed)
- [/] FR-14 Notification record created on status change
- [ ] SIM-05 Notification log screen
- [ ] SIM-04 Generate feedback and ratings for completed orders
- [ ] FR-15 View feedback and ratings
- [ ] FR-17 Dashboard: total orders and revenue
- [ ] FR-18 Popular items
- [ ] FR-19 Revenue chart
- [ ] Analytics functions with unit tests (including empty data)
- [ ] Error message with retry for failed queries
- [ ] FR-10 Menu search by name
- [ ] FR-16 Filter feedback by star rating
- [ ] Record client feedback in `CLIENT_MEETINGS.md`

## Target 3: 13 to 15 Nov (testing and report)
- [ ] Test plan with expected and actual results
- [ ] Unit tests for login/hashing and role check
- [ ] Manual UI testing of every Must/High requirement
- [ ] Security review (no credentials in the repo, hashes only, role checks)
- [ ] Technical report (requirements, design, implementation, testing, change management, evaluation, references)
- [ ] GenAI usage declaration
- [ ] Viva preparation: be able to explain every part of the code

## Target 4: 16 Nov
- [ ] Final commit and submission
- [ ] Demo and viva

## Change requests
Add new items here as the client asks for changes, with a CR number and a link to the matching entry in `CHANGELOG.md`.
