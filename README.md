# Complaint Management App

This Android application provides a platform for users to submit complaints and for administrators to manage those complaints.  It's built using Java for the Android application logic and Firebase for data storage and synchronization.


## Introduction

This app addresses the need for a streamlined complaint management system.  It separates user and admin functionalities for efficient handling of issues.

## Features

*User App:*

* User registration and login.
* Submit complaints with title, description, and (optionally) category.
* View the status of submitted complaints (e.g., Pending, In Progress, Resolved, Completed).

*Admin App:*

* Secure admin login.
* View a list of all complaints.
* Update the status of complaints.
* Add comments to complaints for internal communication or updates to the user.
* Real-time synchronization of data between the app and Firebase.

## Screenshots

(Include screenshots of both the User and Admin apps here.  Show key screens like complaint submission, complaint list, and admin update screen.  Animated GIFs showing real-time updates are even better!)

## Technologies

* *Android Studio:* The primary IDE for Android development.
* *Java:* The programming language used for the Android application logic.
* *Firebase:*
    * *Realtime Database:* Used for storing complaint data and enabling real-time updates.  This database structure allows for quick synchronization of information across devices.
    * *Authentication:* Handles user and admin authentication, ensuring secure access to the app's features.

## Usage

### User App

1. *Registration/Login:*  Create an account or log in with existing credentials.
2. *Submit Complaint:* Fill in the complaint details (title, description, category - if applicable).
3. *View Status:*  Track the status of your submitted complaints in the app.

### Admin App

1. *Admin Login:* Log in using administrator credentials.
2. *View Complaints:*  Browse the list of all submitted complaints.
3. *Update Status:* Select a complaint and choose a new status from the available options.
4. *Add Comment:* Add internal comments to a complaint for tracking or communication.

##Contributions
Feel free to fork the repository, open issues, or submit pull requests. Please ensure that you follow the code style and include appropriate comments.

## Data Structure (Firebase)

The data in Firebase is structured as follows:

```json
{
  "complaints": {
    "complaint_id_1": {  // Unique ID for each complaint
      "userId": "user_id_1", // ID of the user who submitted the complaint
      "title": "Complaint Title",
      "description": "Complaint Description",
      "category": "Category (optional)",
      "status": "Pending", // or "In Progress", "Resolved", "Completed"
      "timestamp": 1678886400000, // Example timestamp
      "comments": {  // Comments are stored as a nested object
        "comment_id_1": {
          "comment": "Admin comment text",
          "timestamp": 1678886460000
        },
        "comment_id_2": {
          "comment": "Another admin comment",
          "timestamp": 1678886520000
        }
      }
    },
    "complaint_id_2": { // ... more complaints
      // ...
    }
  }
}


