# Project Plan

Upgrade 'Chor Polish' (Raja Mantri Chor Sipahi) to a real-time multiplayer game. 
Backend: Node.js, Express, Socket.IO, MongoDB Atlas.
Frontend: Kotlin, Jetpack Compose, Retrofit, Hilt, Socket.IO client.
Features: 4-player rooms (6-digit code), real-time role assignment, secure reveals, server-side scoring, reconnection logic, admin dashboard.

## Project Brief

# Project Brief: ChorPolish Multiplayer

A real-time, multiplayer evolution of the "Raja Mantri Chor Sipahi" game, transitioning from a shared-device experience to a synchronized online platform for 4 players.

### Features
*   **Real-Time Room System**: Players can create or join private game lobbies using a unique 6-digit code, facilitating seamless 4-player matchmaking.
*   **Synchronized Role Distribution**: Server-side shuffling and secure assignment of roles (Raja, Mantri, Chor, Sipahi) with real-time "tap-to-reveal" states across all clients.
*   **Interactive Multiplayer Gameplay**: Live guessing phase for the Mantri with automated scoring and round transitions synchronized via Socket.IO.
*   **Session Resilience**: Robust reconnection logic with a 2-minute window, ensuring players can rejoin active matches after network interruptions.
*   **Global Competitive Tracking**: A persistent leaderboard system integrated with a cloud backend to track player statistics and global rankings.

### High-Level Technical Stack
*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material 3 with Edge-to-Edge support)
*   **Navigation & Adaptive Strategy**: Strictly state-driven **Jetpack Navigation 3** and **Compose Material Adaptive** library for responsive layouts.
*   **Networking**: **Socket.IO client** for real-time duplex communication and **Retrofit** for RESTful API interactions.
*   **Concurrency**: Kotlin Coroutines & Flow.
*   **Architecture**: MVVM with Hilt for Dependency Injection.
*   **Backend/Persistence**: Node.js/Express with **MongoDB Atlas** for persistent user data and global leaderboards.

## Implementation Steps

### Task_1_DataAndLogic: Implement the data layer and core game logic. This includes Room database entities for game history and statistics, DataStore for settings, and a GameViewModel to handle role shuffling, scoring rules, and game state management.
- **Status:** COMPLETED
- **Updates:** I have completed the implementation of the data layer and core game logic for the Chor Polish app.
- **Acceptance Criteria:**
  - Room database and DAOs are defined
  - GameViewModel manages game state (Raja, Mantri, Sipahi, Chor)
  - Scoring logic for correct/incorrect guesses is implemented

### Task_2_PlayerSetupAndRevealUI: Develop the initial game flow UI using Jetpack Compose and Material 3. Create the player name entry screen and the interactive 'tap-to-reveal' card mechanism for secret role assignment.
- **Status:** COMPLETED
- **Updates:** I have implemented the initial game flow UI.
- **Acceptance Criteria:**
  - Player setup screen allows 4 names
  - Role reveal cards hide roles by default and reveal on tap
  - App follows Material 3 design and uses a vibrant color scheme

### Task_3_GameplayAndStatisticsUI: Implement the core gameplay loop and history tracking. Create the 'Mantri Guess' screen, the round result screen with point swapping logic, and a persistent statistics/leaderboard screen.
- **Status:** COMPLETED
- **Updates:** I have completed the core gameplay loop and history tracking.
- **Acceptance Criteria:**
  - Guessing phase correctly identifies Mantri and prompts for Chor
  - Points are correctly updated in the Room database after each round
  - Statistics screen displays cumulative player performance

### Task_4_PolishingAndVerification: Finalize the application with UI/UX refinements. Implement full edge-to-edge display, adaptive layouts for different screen sizes, an adaptive app icon, and basic animations/sound effects (if applicable). Perform a final 'Run and Verify' check.
- **Status:** COMPLETED
- **Updates:** I have successfully finalized and polished the Chor Polish application.
- **Acceptance Criteria:**
  - App supports edge-to-edge and adaptive layouts
  - Adaptive app icon is generated and applied
  - Final build is stable, no crashes, and meets all project requirements
  - Build pass
  - Make sure all existing tests pass

### Task_5_MultiplayerNetworking: Implement the multiplayer infrastructure. This includes setting up Retrofit for global leaderboard APIs, Socket.IO for real-time gameplay synchronization, and Hilt for dependency injection. Implement the room management logic using 6-digit codes and server-side role assignment events.
- **Status:** COMPLETED
- **Updates:** I have successfully implemented the multiplayer networking infrastructure for Chor Polish.
- **Acceptance Criteria:**
  - Retrofit and Socket.IO integrated with Hilt
  - Room creation/joining via 6-digit code works
  - Real-time role distribution and state events handled via Socket.IO

### Task_6_MultiplayerFlowAndVerify: Build the multiplayer UI and implement session resilience. Create the Lobby screen for matchmaking, adapt existing gameplay screens for synchronized server-driven state, and implement the 2-minute reconnection logic. Perform a final 'Run and Verify' check.
- **Status:** COMPLETED
- **Updates:** I have successfully finalized the Chor Polish multiplayer experience.
- Implemented synchronized multiplayer flow across Lobby, Role Reveal, Guessing, and Result screens using Socket.IO.
- Developed robust 2-minute reconnection logic with session persistence via DataStore and a server-side grace period.
- Shifted core gameplay logic (role assignment, scoring) to the Node.js backend for security and cheat prevention.
- Completed the backend infrastructure in the /backend directory, including Socket.IO handlers, Mongoose models, and a Dockerfile.
- Fixed UI issues like the 'Copy Room Code' button and maintained the Material 3 aesthetic.
- Final verification by the critic_agent confirmed that all multiplayer requirements are met and synchronized.
- **Acceptance Criteria:**
  - Lobby and Gameplay UI are synchronized across devices
  - Reconnection logic works within the 2-minute window
  - Build pass
  - App does not crash
  - Make sure all existing tests pass
  - Critic_agent verifies stability, user requirement alignment, and reports UI issues
- **Duration:** N/A

