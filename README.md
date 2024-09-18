# Hi there! 👋
This project shows how to migrate existing android app to iOS keeping it's logic and UI as it is (almost).

Migration pathway is plotted using [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) approach for sharing data processing logic
and [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) approach for sharing UI

Following this guide assumes that you Android app meets some requirements:
- Room is used as data persistence library
- The project initially used Jetpack Compose as UI library
- Jetpack Navigation is used for handling screens' transitions
- KAPT as annotation processor (migration process to KSP will be described later and may be skipped if KSP is already used)

> [!TIP]
> If You don't have project that meets all requirements, feel free to use mine from initial branch for educational purposes

The migration process will proceed in a few steps:

1. Preparing environment for working with multiplatform
    1. Migrating from kapt to KSP
    2. Making Room multiplatform
2. Creating Compose Multiplatform template
    1. Setting up dependencies
    2. Creating new source sets
    3. Replacing resources
3. Transferring business and UI logic in shared source set
    1. Making platform specific declarations
    2. Setting up DI library
    3. Adapting UI to platform specific features
4. Creating iOS app and connecting to shared source set
    1. Creating iOS application
    2. Connecting shared framework
    3. Extracting UI components

## 1.Preparing environment for working with multiplatform
### 1.1 Migrating from kapt to KSP
To make Room able to work on both platforms we need ksp to be integrated in our project. To read more about setting up KSP, visit [official site](https://kotlinlang.org/docs/ksp-quickstart.html#create-a-processor-of-your-own)
> [!WARNING]
> When working with Kotlin 1.9.*, setup process differs a little from version 2.0.0 +. Changes described in the link above

1. In `libs.versions.toml` add KSP plugin dependency and make sure that ksp version complies with Kotlin version
   ```toml
   [versions]
   kotlin = "2.0.20"
   ksp = "2.0.20-1.0.25"
   
   [plugins]
   ksp = { id = "com.google.devtools.ksp", version.ref ="ksp"}
   ```
2. Sync the project
3. Add this dependency in root `build.gradle.kts` file and in `build.gradle.kts` file of the app module in the `plugin` block. Also, remove all kapt entries and replace them with ksp.
   Ksp will be marked as error until You sync the project. That's ok
   ```kotlin
   // Root build.gradle.kts
   
   plugins {
      alias(libs.plugins.ksp) apply false
   }
   
   // App build.gradle.kts

   plugins {
      alias(libs.plugins.ksp)
      // kotlin("kapt")
   }
   
   dependencies {
      // kapt("androidx.room:room-compiler:2.6.1")
      ksp("androidx.room:room-compiler:2.6.1")
   }

   ```
4. Sync and rebuild project to make sure everything works fine. Some minor errors expected regarding Room. It will be fixed later

### 1.2. Making Room multiplatform
You can set up Room following instructions in [this link](https://developer.android.com/kotlin/multiplatform/room)
We will go through the basic steps briefly
1. Add all necessary dependencies in 'libs.versions.toml' and sync project
   ```toml
     [versions]
     androidx-room = "2.7.0-alpha07"
     androidx-sqlite = "2.5.0-alpha07"
   
     [libraries]
      #Room
     androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "androidx-room"}
     androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "androidx-room"}
     androidx-sqlite-bundled = { group = "androidx.sqlite", name = "sqlite-bundled", version.ref = "androidx-sqlite"}
   
     [plugins]
     room = { id = "androidx.room", version.ref ="androidx-room"}

   ```
2. Apply these plugin and dependencies in `build.gradle.kts` files and add Room schema location. Then sync and rebuild your project to make sure that everything works fine
   ```kotlin
     // Root build.gradle.kts
   
   plugins { 
       alias(libs.plugins.room) apply false
   }
   
   // App build.gradle.kts

   plugins {
      alias(libs.plugins.room)
   }
   
   dependencies {
      // ksp("androidx.room:room-compiler:2.6.1")
      // annotationProcessor("androidx.room:room-compiler:2.6.1")

      implementation(libs.androidx.room.runtime)
      implementation(libs.androidx.sqlite.bundled)
      ksp(libs.androidx.room.compiler)
   } 
   
   room {
      schemaDirectory("$projectDir/schemas")
   }
   
   ```   

3. As per setup instruction we need to provide platform-specific implementations of the database, but we'll do it after changing app module to multiplatform one

## 2. Creating Compose Multiplatform template
### 2.1. Setting up dependencies

To make our app multiplatform, we need to change a bit the dependecies and project template

1. In `libs.versions.toml` add all necessary dependencies and sync the project
   ```toml
     [versions]
     compose-plugin = "1.7.0-beta02"

     [plugins]
        #ComposeMultiplatform
      jetbrainsCompose = { id = "org.jetbrains.compose", version.ref = "compose-plugin" }
      kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }

   ```

    2. In `build.gradle.kts` file apply these dependencies and DO NOT sync project
       ```kotlin
          // Root build.gradle.kts
       
          plugins {
              alias(libs.plugins.kotlinMultiplatform) apply false
              alias(libs.plugins.jetbrainsCompose) apply false
          }
       
          // App build.gradle.kts
    
          plugins {
             // Replace this dependency with KotlinMultiplatform
              // alias(libs.plugins.jetbrains.kotlin.android)
             alias(libs.plugins.kotlinMultiplatform)
             alias(libs.plugins.jetbrainsCompose)
          }
    
          ```
3. After all dependencies are set, we need to configure our source sets in the module. In the app `build.gradle.kts` write the following code and remove `kotlinOptions` block within `android`. Then sync the project
   ```kotlin
      plugins {
        // some plugins
      }
      // Write kotlin block to configure targets
      kotlin {
          androidTarget {
              compilations.all {
                  kotlinOptions {
                      jvmTarget = "1.8"
                  }
              }
          }
      }
      
      android {
        // Remove this block
        // kotlinOptions {
        //      jvmTarget = "1.8"
        // }
      }
   ```
4. Continue to add more targets in the `kotlin` block
   ```kotlin
      plugins {
        ...
      }
      kotlin {
          androidTarget {
              compilations.all {
                  kotlinOptions {
                      jvmTarget = "1.8"
                  }
              }
          }
         // Configure iOS tragets for Arm and x64 processors and simulator
         listOf(
           iosX64(),
           iosArm64(),
           iosSimulatorArm64()
         ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "Shared"
                isStatic = false
            }
         }
         // Configure dependencies for each source set
         sourceSets {
            androidMain.dependencies {
               implementation(compose.preview)
               implementation(libs.androidx.activity.compose)
            }
            commonMain.dependencies {
               implementation(compose.runtime)
               implementation(compose.foundation)
               implementation(compose.material3)
               implementation(compose.ui)
               implementation(compose.components.resources)
               implementation(compose.components.uiToolingPreview)
               
               // Libraries to make common navigation and view models
               implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
               implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
               
               // 2 of 3 Room dependencies now in common source set
               implementation(libs.androidx.room.runtime)
               implementation(libs.androidx.sqlite.bundled)
            }
         }
      }
         
   android {
      ...
   }
   
   ```

4. In the same file in `android` block define placement of common resources
   and android res folder and manifest file
   ```kotlin
      android {
   
            sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
            sourceSets["main"].res.srcDirs("src/androidMain/res")
            sourceSets["main"].resources.srcDirs("src/commonMain/resources")
   
           defaultConfig { 
                ...
           }        
      }
   ```   

5. In the same file in `android` block remove room `runtime` and sqlite `bundled` dependencies and replace room `compiler` dependency
   to the bottom in separate `dependencies` block
   ```kotlin
      android {
   
            dependencies {
                // Remove
                // implementation(libs.androidx.room.runtime)
                // implementation(libs.androidx.sqlite.bundled)
                // Replace to the bottom of file
                // ksp(libs.androidx.room.compiler)
            }        
      }
   
      dependencies {
          ksp(libs.androidx.room.compiler)
      }    
   ```   

6. At the end of this file put `task("testClasses")` to avoid this task missing during compilation to avoid errors
7. Sync the project

### 2.2. Creating new source sets
With all changes made, our app module should look like this 

![image](https://github.com/user-attachments/assets/a5475d75-db94-4f61-8d4b-9b463e4d34a2)


Now, when we defined all our source sets we need to create appropriate folders. Android Studio can help with this. You just need to right-click on src folder in app module and then click `new` -> `directory`

If everything was configured correctly, You will see the following:

![image](https://github.com/user-attachments/assets/0e2a3d9c-125a-4011-b567-275476dfa7a3)


We need these directories:
- `AndroidMain` ( kotlin & resources directories )
- `CommonMain` ( kotlin & resources directories )
- `iosMain` ( kotlin & resources directories )

When all directories created, your app module structure should look like this

![image](https://github.com/user-attachments/assets/185014b0-78ed-4651-8047-6f4a2ca70a9f)


### 2.3. Replacing resources

Now You need to place all necessary files from `main` directory to `androidMain`

1. Select all sub folders inside of `main` `res` folder and drag it to `androidMain` `res` folder
2. Select `AndroidManifest` file and drag it in `androidMain` directory
3. In `androidMain` `kotlin` directory create packages `com.standalone.migrationwithcompose` and place `MainActivity` and `ComposeApplication` there
4. In `commonMain` `kotlin` directory create packages `com.standalone.migrationwithcompose` and place there following:
    - `db` package with its content
    - `navigation` package with its content
    - `ui.theme` packages with content
    - `Login` and `Main` Screens with `MainScreenViewModel`
5. In `Theme.kt` remove `colorScheme` variable and place `LightColorScheme` inside of `MaterialTheme` and remove invalid imports

To be continued
