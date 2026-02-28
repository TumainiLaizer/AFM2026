# AFM2026 - African Football Manager 2026
## FAME Africa™

A lightweight football manager simulation game for African leagues with a focus on African culture and drama.

AFM2026 – UI/UX Flow Design
African Football Manager 2026 – FAME Africa™
📱 Platform & Navigation Philosophy
Platform: Mobile-first, responsive design (also desktop compatible)

Navigation: Bottom tab bar (mobile) / Left sidebar (desktop)

Font: Clean, readable with African decorative elements in headers

Motion: Subtle animations inspired by African patterns/geometry

🏠 MAIN NAVIGATION FLOW
1. LAUNCH SCREEN
text
App Icon → Splash Screen (FAME Africa logo with African pattern)
↓
Main Menu:
- [Continue Career]
- [New Career]
- [Load Game]
- [Settings]
- [Credits]
- [Exit]
2. NEW CAREER SETUP FLOW
text
[New Career] → Country Selection (African map highlight)
↓
League Selection (Tanzania, Nigeria, Ghana, etc.)
↓
Club Selection (with logo, reputation, budget preview)
↓
Manager Creation:
- Name (default: "Tumaini Joseph")
- Nationality
- Age
- Coaching License
- Starting Reputation (Local)
↓
Difficulty Settings (based on board/fan expectations)
↓
[Start Career] → Loading into **Home Dashboard**
🎮 CAREER MODE – CORE NAVIGATION
BOTTOM TAB BAR (5 Tabs)
TAB 1: 📊 DASHBOARD (Home)
text
Dashboard →
├── 📈 Club Overview (Morale, Form, Next Match)
├── 🏆 Objectives (Board/Fan expectations)
├── 📰 Latest News (Headlines, rumors, interviews)
├── ⚠️ Notifications (Inbox style)
├── 🎯 Quick Actions:
│   - [Pick Team]
│   - [Next Match]
│   - [Handle Interview]
│   - [Board Request]
└── 📅 Upcoming Fixtures (3 next matches)
TAB 2: 👥 SQUAD
text
Squad →
├── 👤 Player List (Grid/List view)
│   ├── Tap Player → Player Profile
│   │   ├── Attributes
│   │   ├── Contract
│   │   ├── Form/Training
│   │   ├── [Transfer List]
│   │   └── [Offer Contract]
│   └── [Sort by]: Position/Rating/Form/Value
├── ⚙️ Tactics
│   ├── Formation Picker (4-4-2, 4-2-3-1, 3-5-2)
│   ├── Drag & Drop starting XI
│   ├── Set Pieces
│   └── [Save Tactic]
├── 💪 Training
│   ├── Drill Types (Fitness, Technical, Tactical)
│   ├── Injury Risk Indicator
│   └── [Auto-Train]
└── 🩺 Medical
   ├── Injury Report
   └── Recovery Timeline
TAB 3: 🔄 TRANSFERS
text
Transfers →
├── 🔍 Scouting
│   ├── [Search Players] (Filters: Position, Age, Value)
│   ├── Scout Reports (Star rating, potential)
│   └── [Assign Scout]
├── 🤝 Negotiations
│   ├── Active Offers (In/Out)
│   ├── Contract Talks
│   └── Agent Discussions
├── 📜 Loans
│   ├── Loan Offers
│   └── Loan List
├── 💰 Budget Overview
│   ├── Available Funds
│   └── Wage Budget
└── 🗓️ Transfer Window Countdown
TAB 4: 🏢 CLUB
text
Club →
├── 💵 Finances
│   ├── Balance Sheet
│   ├── Sponsors (Visit Tanzania, etc.)
│   └── [Renegotiate Sponsorships]
├── 🏟️ Infrastructure
│   ├── Stadium Upgrades
│   ├── Youth Academy
│   └── Training Facilities
├── 👥 Staff
│   ├── Assistant Manager
│   ├── Scouts
│   └── Medical Team etc
├── 📊 Statistics
│   ├── League Table
│   ├── Top Scorers/Top Assisters/Top GK (Cleansheets)
│   └── Team Stats
└── 🏆 History
   ├── Trophy Cabinet
   └── Club Legends
TAB 5: 📰 WORLD
text
World →
├── 🌍 Leagues
│   ├── Standings
│   ├── Fixtures
│   └── [Switch League View]
├── 🌐 Continental
│   ├── CAF Champions League
│   ├── CAF Confederation Cup
│   └── AFCON Qualifiers
├── 🇹🇿 National Teams
│   ├── Squad
│   ├── Fixtures
│   └── [Apply for Job]
├── 🗞️ News Feed
│   ├── Filter by: Club/League/National
│   └── [Subscribe to Journalist]
└── 🎭 Drama Feed
   ├── Transfer Rumors
   ├── Board Conflicts
   └── Fan Reactions
⚽ MATCH DAY FLOW
text
[Next Match] from Dashboard →
Pre-Match Screen:
├── Opponent Analysis (Formation, Key Players)
├── Team Selection (Drag & Drop)
├── Tactics (Attacking/Defensive mentality)
├── Team Talk (Motivational options)
└── [Start Match] →
MATCH INTERFACE (3 Views)
View 1: 📊 Live Commentary (Default)
text
[Minute by Minute Log]
├── ⚽ Key Events (Goals, Cards, Subs)
├── 📊 Match Stats (Possession, Shots, Corners)
├── 🔄 [Make Substitution]
├── ⚙️ [Change Tactics]
└── ⏭️ [Sim to Next Event] / [Fast Forward]
View 2: 🎮 Tactical View
text
Top-Down Pitch View
├── Player Circles with numbers and position
├── Ball Movement
├── Heat Maps
└── Tactical Instructions Panel
View 3: 📈 Data View
text
Live Statistics
├── Player Ratings
├── Passing Networks
├── Expected Goals (xG)
└── Performance Trends
text
Match Ends →
Post-Match Screen:
├── Result Summary
├── Player Ratings
├── Match Events Timeline
├── [Press Conference] → Interview Choices
└── [Continue] → Back to Dashboard
🎭 DRAMA & DECISION FLOWS
1. INTERVIEW FLOW
text
Notification: "Journalist Azam Sports requests interview"
↓
[Accept/Decline] → If Accept:
↓
Interview Screen:
├── Journalist Info (Personality: Neutral/Hostile/Friendly)
├── Question 1: "What do you think about recent form?"
│   └── Options: Confident/Humble/Defensive
├── Question 2: "Any transfer plans?"
│   └── Options: Reveal/Tactical/Deflect
└── [Submit] → Impact on: Media Relations, Board Trust, Fan Confidence
2. BOARD REQUEST FLOW
text
Notification: "Board wants youth development focus"
↓
Board Request Screen:
├── Request Details
├── Accept/Reject/Negotiate
├── Consequences Preview
│   - Accept: Budget boost, reputation gain
│   - Reject: Satisfaction drop
└── [Confirm Decision]
3. PLAYER INTERACTION FLOW
text
Player Profile → [Talk to Player]
↓
Reason Selection:
- Boost Morale
- Discuss Playing Time
- Contract Concerns
- Discipline Issue
↓
Dialog Options (based on player personality)
↓
Outcome: Morale Change/Relationship Impact
⚙️ SETTINGS & PREFERENCES FLOW
text
Settings →
├── 🎮 Game Settings
│   ├── Difficulty
│   ├── Simulation Speed
│   └── Auto-Save Frequency
├── 🌐 Regional Settings
│   ├── Currency (USD/TZS/NGN)
│   ├── Language
│   └── Date Format
├── 🎨 Display
│   ├── Theme (Light/Dark/African)
│   └── Text Size
├── 🔊 Audio
│   ├── Commentary Volume
│   ├── Crowd Sounds
│   └── Music (African soundtrack toggle)
└── 💾 Save/Load
   ├── [Save Game]
   ├── [Load Game]
   └── [Export Save]
📱 MOBILE-SPECIFIC UX PATTERNS
GESTURES:
Swipe left/right between player cards

Pull down to refresh news/table

Long press on player for quick actions

Pinch zoom on tactical view

OFFLINE MODE:
Basic squad management

Training planning

Contract negotiations

Sync when online

NOTIFICATION TYPES:
🔔 Match Reminders (1 hour before)

📰 Breaking News

💰 Transfer Offers (24h expiry)
🩺 Injury Updates

🏆 Trophy Won

📊 End of Season Summary

🎨 VISUAL DESIGN SYSTEM
COLOR PALETTE:
Primary: African Sunset Orange (#E25822)

Secondary: Savannah Green (#2E8B57)

Accent: Gold (#FFD700)

Background: Warm White / Dark Brown

Success: Green (#4CAF50)

Warning: Yellow (#FFC107)

Error: Red (#F44336)

COMPONENTS:
Cards: Rounded corners, subtle shadow

Buttons: Filled (primary), Outlined (secondary)

Badges: For form (WWDL), injury status

Progress Bars: For attributes, morale, fitness

Player Cards: Photo, flag, position badge, rating

ICON SET:
Custom African-inspired icons

Clear, recognizable for key actions

Color-coded by category

🔄 DATA FLOW & PERFORMANCE
LAZY LOADING:
Player photos load on scroll

League tables paginated

Match commentary streams

CACHING:
Squad data cached locally

League standings updated daily

News articles stored offline

SYNC:
Cloud save option

Cross-device progress

Leaderboards (optional)

🚀 ONBOARDING FLOW (First-Time Users)
text
App Start → Welcome Video (30s African football highlights)
↓
Quick Tutorial (Swipe through 3 screens):
1. "Manage Your Squad" - Drag players
2. "Make Decisions" - Interview example
3. "Win Trophies" - Trophy cabinet preview
↓
[Skip] / [Continue to Setup]
📊 ACCESSIBILITY FEATURES
Text Size: Adjustable (Small/Medium/Large)

Color Blind Mode: Alternative palettes

VoiceOver Support: Screen reader compatible

Subtitles: For commentary/audio

High Contrast Mode

🎯 USER JOURNEY EXAMPLES
JOURNEY 1: THE UNDERDOG STORY
text
Local Tanzanian Club → Relegation Battle → Survive → 
Youth Development → Cup Run → Continental Qualification → 
African Champions
JOURNEY 2: NATIONAL HERO
text
Club Success → National Team Offer → AFCON Qualification → 
AFCON Victory 
text
Big Club in Crisis → Financial Issues → Sell Stars → 
Build Youth → Return to Glory → Continental Dominance
🔧 TECHNICAL UI STACK (SUGGESTED)
Framework: React Native / Flutter (cross-platform)

State Management: Redux / Provider

Database: SQLite (local), Firebase (cloud sync)

Animations: Lottie / Rive

Charts: Victory Native / MPAndroidChart

Navigation: React Navigation / Flutter Navigator

This UI/UX flow ensures:

Intuitive navigation for both casual and hardcore players

African cultural integration in every screen

Performance optimization for low-end devices

Schema alignment with all database tables

Drama and storytelling at the forefront
