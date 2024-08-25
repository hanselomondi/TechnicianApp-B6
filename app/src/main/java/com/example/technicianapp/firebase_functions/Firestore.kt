package com.example.technicianapp.firebase_functions

import android.util.Log
import com.example.technicianapp.models.Technician
import com.example.technicianapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


suspend fun saveTechnicianToFirestore(
    uid: String,
    tech: Technician
): Result<Unit> { // TODO change return T
    val db = FirebaseFirestore.getInstance()

    return try {
        // Save the tech profile to Firestore with the document ID set to the Tech's UID
        db.collection("Technicians").document(uid).set(tech).await()

        Result.success(Unit) // Return success if the operation completes without exceptions
    } catch (e: Exception) {
        // Return failure with an error message if an exception occurs
        Result.failure(e)
    }
}


suspend fun getUserProfileFromFirestore(uid: String): Result<User> {
    val db = FirebaseFirestore.getInstance()

    return try {
        // Get the user profile from Firestore using the User's UID
        val user =
            db.collection("Technicians").document(uid).get().await().toObject(User::class.java)

        if (user != null) {
            Result.success(user) // Return success if the operation completes without exceptions
        } else {
            Result.failure(NullPointerException("User profile is null"))
        }
    } catch (e: Exception) {
        // Return failure with an error message if an exception occurs
        Result.failure(e)
    }
}


suspend fun fetchServices(): Result<Map<String, List<String>>> {

    return try {
        val firestore = FirebaseFirestore.getInstance()
        val doc = firestore.collection("Categories").document("Services").get().await()

        if (doc.exists()) {
            val data = doc.data ?: emptyMap<String, Any>()

            val servicesMap = data.mapValues { (_, value) ->
                (value as? List<*>)?.map { it.toString() } ?: emptyList()
            }

            Log.d(
                "Firestore",
                "fetchServices: Services fetched successfully. ${servicesMap.toSortedMap()}"
            )

            Result.success(servicesMap.toSortedMap())
        } else {
            Log.d("Firestore", "fetchServices: Document does not exist.")

            Result.failure(Exception("Document does not exist"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}


suspend fun addServicesToTechnicianFirestore(selectedSkills: Map<String, List<String>>): Result<String> {

    return try {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "Control_T1"
        val docRef = db.collection("Technicians").document(uid)

        docRef.set(mapOf("Services_Offered" to selectedSkills), SetOptions.merge())
            .await()

        Log.d(
            "Firestore",
            "addServicesToTechnicianFirestore: Skills successfully saved to Firestore for user $uid"
        )

        Result.success("Services successfully saved to Firestore for user $uid")
    } catch (e: Exception) {
        Log.e(
            "Firestore",
            "addServicesToTechnicianFirestore: Error saving Services to Firestore",
            e
        )

        Result.failure(e)
    }
}

suspend fun techCreateOrSendMessage(
    clientId: String,
    techId: String,
    message: String
): Result<String> {
    val db = FirebaseFirestore.getInstance()

    val techDocRef =
        db.collection("Technicians").document(techId).collection("Chats").document(clientId)
    val clientDocRef =
        db.collection("Clients").document(clientId).collection("Chats").document(techId)

    val randomTail = System.currentTimeMillis().toString()
    val techUniqueKey = "Tech_$randomTail"

    val techMessageData = mapOf(
        techUniqueKey to mapOf(
            "message" to message,
            "time" to Timestamp.now()
        )
    )

    return try {
        // Suspend until the set operation is complete for the tech collection
        techDocRef.set(techMessageData, SetOptions.merge()).await()
        Log.d("Firestore", "Tech Message added to Tech Collection successfully.")

        // Suspend until the set operation is complete for the client collection
        clientDocRef.set(techMessageData, SetOptions.merge()).await()

        Log.d("Firestore", "Tech Message added to Client Collection successfully.")

        Result.success("Tech Message added to Firestore successfully.")
    } catch (exception: Exception) {
        Log.e("Firestore", "Error adding tech message: $exception")

        Result.failure(Exception("Error adding tech message: $exception"))
    }
}

suspend fun clientCreateOrSendMessage(
    clientId: String,
    techId: String,
    message: String
): Result<String> {
    val db = FirebaseFirestore.getInstance()

    val techDocRef =
        db.collection("Technicians").document(techId).collection("Chats").document(clientId)
    val clientDocRef =
        db.collection("Clients").document(clientId).collection("Chats").document(techId)

    val randomTail = System.currentTimeMillis().toString()
    val clientUniqueKey = "Client_$randomTail"

    val clientMessageData = mapOf(
        clientUniqueKey to mapOf(
            "message" to message,
            "time" to Timestamp.now()
        )
    )

    return try {
        // Suspend until the set operation is complete for the tech collection
        techDocRef.set(clientMessageData, SetOptions.merge()).await()
        Log.d("Firestore", "Client Message added to Tech Collection successfully.")

        // Suspend until the set operation is complete for the client collection
        clientDocRef.set(clientMessageData, SetOptions.merge()).await()

        Log.d("Firestore", "Client Message added to Client Collection successfully.")

        Result.success("Client Message added to Firestore successfully.")
    } catch (exception: Exception) {
        Log.e("Firestore", "Error adding Client message: $exception")

        Result.failure(Exception("Error adding Client message: $exception"))
    }
}


suspend fun getClientsChatList(techId: String): Result<List<DocumentSnapshot>> {
    val db = FirebaseFirestore.getInstance()

    val clientsChatCollection = db.collection("Technicians").document(techId).collection("Chats")

    return try {
        val result = withContext(Dispatchers.IO) {
            clientsChatCollection.get().await()
        }

        if (!result.isEmpty) {
            Log.d("Firestore", "Clients chat list fetched successfully.\n${result.documents}")

            Result.success(result.documents)
        } else {
            Log.e("Firestore", "No clients found.")

            Result.failure(Exception("No clients found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}


// TODO ChatGPT prompt
/*Now that I have a functiont that gets the client documents from the chat Collection, I need a screen and a view model

The screen, on entering composition calls a view model function which in turn calls the firesore function:
suspend fun getClientsChatList(techId: String): Result<List<DocumentSnapshot>> {
    val db = FirebaseFirestore.getInstance()

    val clientsChatCollection = db.collection("Technicians").document(techId).collection("Chats")

    return try {
        val result = withContext(Dispatchers.IO) {
            clientsChatCollection.get().await()
        }

        if (!result.isEmpty) {
            Log.d("Firestore", "Clients chat list fetched successfully.\n${result.documents}")

            Result.success(result.documents)
        } else {
            Log.e("Firestore", "No clients found.")

            Result.failure(Exception("No clients found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

To get all the chat documents

The screen then only displays a row for each client
The row shows the client ID which is the document ID and the latest message according to the time field. this is the function that adds a message, so you can know the structure of a message in Firestore
suspend fun techCreateOrSendMessage(clientId: String, techId: String, message: String) {
    val db = FirebaseFirestore.getInstance()

    val techDocRef =
        db.collection("Technicians").document(techId).collection("Chats").document(clientId)
    val clientDocRef =
        db.collection("Clients").document(clientId).collection("Chats").document(techId)

    val randomTail = System.currentTimeMillis().toString()
    val techUniqueKey = "Tech_$randomTail"

    val techMessageData = mapOf(
        techUniqueKey to mapOf(
            "message" to message,
            "time" to Timestamp.now()
        )
    )

    try {
        // Suspend until the set operation is complete for the tech collection
        techDocRef.set(techMessageData, SetOptions.merge()).await()
        Log.d("Firestore", "Tech Message added to Tech Collection successfully.")

        // Suspend until the set operation is complete for the client collection
        clientDocRef.set(techMessageData, SetOptions.merge()).await()
        Log.d("Firestore", "Tech Message added to Client Collection successfully.")
    } catch (exception: Exception) {
        Log.e("Firestore", "Error adding tech message: $exception")
    }
}

so the row shows the client ID and the latest message. Kind of like how an*/


// TODO only use to add more skills
/*
suspend fun addSkillsToFirestore(): String {
    val firestore = FirebaseFirestore.getInstance()

    // Example data to be added
    val skillsData = mapOf(
        "Floor Work" to listOf(
            "Tile Laying",
            "Wood Tile Laying",
            "Vinyl Flooring",
            "Carpet Installation",
            "Laminate Flooring",
            "Epoxy Flooring",
            "Concrete Flooring",
            "Parquet Flooring",
            "Rubber Flooring",
            "Terrazzo Flooring"
        ),
        "Electrical Work" to listOf(
            "Wiring Installation",
            "Lighting Fixtures",
            "Electrical Panel Upgrades",
            "Circuit Breaker Repair",
            "Outlet Installation",
            "Switch Replacement",
            "Generator Installation",
            "Smoke Detector Installation",
            "Ceiling Fan Installation",
            "Electrical Troubleshooting"
        ),
        "Plumbing" to listOf(
            "Pipe Installation",
            "Leak Repair",
            "Faucet Replacement",
            "Toilet Repair",
            "Shower Installation",
            "Water Heater Repair",
            "Sink Installation",
            "Drain Cleaning",
            "Garbage Disposal Installation",
            "Backflow Prevention"
        ),
        "Painting" to listOf(
            "Interior Painting",
            "Exterior Painting",
            "Wall Priming",
            "Trim Painting",
            "Ceiling Painting",
            "Cabinet Painting",
            "Deck Staining",
            "Faux Finishes",
            "Wallpaper Removal",
            "Drywall Patching"
        ),
        "Carpentry" to listOf(
            "Cabinet Making",
            "Furniture Repair",
            "Door Installation",
            "Window Framing",
            "Custom Shelving",
            "Woodwork Restoration",
            "Trim Carpentry",
            "Deck Building",
            "Staircase Construction",
            "Wooden Flooring"
        ),
        "Masonry" to listOf(
            "Brick Laying",
            "Stone Work",
            "Block Work",
            "Concrete Paving",
            "Fireplace Construction",
            "Retaining Walls",
            "Masonry Repair",
            "Tile Installation",
            "Concrete Slab Work",
            "Stucco Application"
        ),
        "Roofing" to listOf(
            "Shingle Replacement",
            "Roof Repair",
            "Gutter Installation",
            "Roof Inspection",
            "Flashing Repair",
            "Skylight Installation",
            "Roof Ventilation",
            "Roof Coating",
            "Tile Roofing",
            "Metal Roofing"
        ),
        "HVAC" to listOf(
            "Air Conditioner Installation",
            "Heater Repair",
            "Duct Cleaning",
            "Thermostat Installation",
            "HVAC Maintenance",
            "Furnace Repair",
            "Air Purifier Installation",
            "Humidifier Installation",
            "HVAC Troubleshooting",
            "Ductwork Installation"
        ),
        "Landscaping" to listOf(
            "Lawn Mowing",
            "Garden Design",
            "Tree Pruning",
            "Sod Installation",
            "Mulching",
            "Irrigation System Installation",
            "Landscape Lighting",
            "Flower Bed Installation",
            "Paver Patios",
            "Shrub Trimming"
        ),
        "Home Improvement" to listOf(
            "Drywall Installation",
            "Insulation Installation",
            "Window Replacement",
            "Door Installation",
            "Basement Finishing",
            "Attic Insulation",
            "Kitchen Remodeling",
            "Bathroom Remodeling",
            "Closet Organization",
            "Home Additions"
        ),
        "Pest Control" to listOf(
            "Rodent Control",
            "Insect Extermination",
            "Termite Treatment",
            "Bed Bug Treatment",
            "Ant Control",
            "Cockroach Control",
            "Flea Control",
            "Wasp Nest Removal",
            "Spider Control",
            "Wildlife Removal"
        ),
        "Cleaning Services" to listOf(
            "House Cleaning",
            "Carpet Cleaning",
            "Window Washing",
            "Upholstery Cleaning",
            "Deep Cleaning",
            "Post-Construction Cleaning",
            "Office Cleaning",
            "Move-Out Cleaning",
            "Pressure Washing",
            "Tile and Grout Cleaning"
        ),
        "Security Systems" to listOf(
            "Alarm Installation",
            "CCTV Installation",
            "Motion Sensor Installation",
            "Security Camera Setup",
            "Access Control Systems",
            "Security System Maintenance",
            "Smart Locks Installation",
            "Intercom Systems",
            "Home Automation",
            "Burglar Alarm Systems"
        ),
        "Appliance Repair" to listOf(
            "Refrigerator Repair",
            "Washing Machine Repair",
            "Dryer Repair",
            "Oven Repair",
            "Dishwasher Repair",
            "Microwave Repair",
            "Freezer Repair",
            "Range Hood Repair",
            "Garbage Disposal Repair",
            "Air Conditioner Repair"
        ),
        "Handyman Services" to listOf(
            "Picture Hanging",
            "Furniture Assembly",
            "Minor Repairs",
            "Shelf Installation",
            "Light Fixture Replacement",
            "Door Lock Repair",
            "Curtain Rod Installation",
            "Outlet Repair",
            "Cabinet Adjustment",
            "Minor Electrical Repairs"
        ),
        "Concrete Work" to listOf(
            "Concrete Pouring",
            "Concrete Repair",
            "Driveway Installation",
            "Sidewalk Installation",
            "Concrete Stamping",
            "Foundation Repair",
            "Concrete Sealing",
            "Patio Installation",
            "Curb Installation",
            "Slab Removal"
        ),
        "Gutter Services" to listOf(
            "Gutter Cleaning",
            "Gutter Repair",
            "Gutter Installation",
            "Gutter Guard Installation",
            "Downspout Installation",
            "Gutter Inspection",
            "Leaf Removal",
            "Gutter Replacement",
            "Water Damage Prevention",
            "Gutter System Maintenance"
        ),
        "Pool Services" to listOf(
            "Pool Cleaning",
            "Pool Repair",
            "Pool Installation",
            "Water Testing",
            "Pool Maintenance",
            "Pool Heater Installation",
            "Pool Equipment Repair",
            "Pool Cover Installation",
            "Pool Tile Cleaning",
            "Pool Leak Detection"
        ),
        "Fencing" to listOf(
            "Fence Installation",
            "Fence Repair",
            "Wooden Fencing",
            "Metal Fencing",
            "Vinyl Fencing",
            "Chain Link Fencing",
            "Fence Painting",
            "Gate Installation",
            "Privacy Fencing",
            "Security Fencing"
        ),
        "Exterior Cleaning" to listOf(
            "Power Washing",
            "Gutter Cleaning",
            "Roof Cleaning",
            "Siding Cleaning",
            "Deck Cleaning",
            "Fence Cleaning",
            "Concrete Cleaning",
            "Patio Cleaning",
            "Driveway Cleaning",
            "Window Cleaning"
        ),
        "Home Theater Installation" to listOf(
            "TV Mounting",
            "Home Theater Setup",
            "Speaker Installation",
            "Cable Management",
            "Projector Installation",
            "Sound System Setup",
            "Home Automation Integration",
            "Remote Control Programming",
            "AV Equipment Setup",
            "Wall Mount Installation"
        ),
        "Car Detailing" to listOf(
            "Interior Cleaning",
            "Exterior Wash",
            "Waxing",
            "Polishing",
            "Engine Cleaning",
            "Tire Shine",
            "Upholstery Cleaning",
            "Window Cleaning",
            "Paint Protection",
            "Headlight Restoration"
        ),
        "Furniture Assembly" to listOf(
            "Bed Assembly",
            "Table Assembly",
            "Chair Assembly",
            "Shelf Assembly",
            "Cabinet Assembly",
            "Desk Assembly",
            "Entertainment Center Assembly",
            "Dresser Assembly",
            "Bookcase Assembly",
            "Office Furniture Assembly"
        ),
        "Renovations" to listOf(
            "Kitchen Renovation",
            "Bathroom Renovation",
            "Basement Renovation",
            "Attic Renovation",
            "Living Room Renovation",
            "Bedroom Renovation",
            "Office Renovation",
            "Home Theater Renovation",
            "Exterior Renovation",
            "Flooring Renovation"
        ),
        "Roof Maintenance" to listOf(
            "Roof Inspection",
            "Roof Repair",
            "Shingle Replacement",
            "Gutter Cleaning",
            "Flashing Repair",
            "Roof Coating",
            "Roof Sealing",
            "Roof Ventilation",
            "Skylight Repair",
            "Roof Leak Repair"
        ),
        "Insulation" to listOf(
            "Attic Insulation",
            "Wall Insulation",
            "Basement Insulation",
            "Floor Insulation",
            "Spray Foam Insulation",
            "Blown-In Insulation",
            "Fiberglass Insulation",
            "Reflective Insulation",
            "Insulation Removal",
            "Insulation Installation"
        ),
        "Landscape Design" to listOf(
            "Garden Design",
            "Hardscape Design",
            "Plant Selection",
            "Irrigation Design",
            "Outdoor Lighting Design",
            "Patio Design",
            "Walkway Design",
            "Pond Design",
            "Outdoor Kitchen Design",
            "Fire Pit Design"
        ),
        "Home Staging" to listOf(
            "Furniture Arrangement",
            "Décor Placement",
            "Color Coordination",
            "Lighting Setup",
            "Accessory Placement",
            "Cleaning and Organizing",
            "Curb Appeal Enhancement",
            "Wall Art Installation",
            "Room Styling",
            "Property Preparation"
        ),
        "Drywall Services" to listOf(
            "Drywall Installation",
            "Drywall Repair",
            "Drywall Taping",
            "Drywall Mudding",
            "Drywall Sanding",
            "Drywall Texturing",
            "Ceiling Drywall Installation",
            "Drywall Patching",
            "Drywall Removal",
            "Drywall Painting"
        ),
        "Event Setup" to listOf(
            "Tent Setup",
            "Table Setup",
            "Chair Setup",
            "Décor Installation",
            "Audio Equipment Setup",
            "Lighting Setup",
            "Stage Setup",
            "Floor Covering",
            "Catering Setup",
            "AV Equipment Setup"
        ),
        "Cleaning Services" to listOf(
            "Carpet Cleaning",
            "Upholstery Cleaning",
            "Window Washing",
            "Pressure Washing",
            "Post-Construction Cleaning",
            "Office Cleaning",
            "Move-In/Move-Out Cleaning",
            "Deep Cleaning",
            "Tile and Grout Cleaning",
            "Seasonal Cleaning"
        ),
        "Gutter Installation" to listOf(
            "Gutter Installation",
            "Gutter Repair",
            "Downspout Installation",
            "Gutter Guard Installation",
            "Gutter Cleaning",
            "Gutter Replacement",
            "Gutter Sealing",
            "Downspout Extension",
            "Gutter Inspection",
            "Gutter Maintenance"
        ),
        "Pest Control" to listOf(
            "Termite Treatment",
            "Rodent Control",
            "Bed Bug Extermination",
            "Ant Control",
            "Cockroach Control",
            "Flea Control",
            "Wasp Nest Removal",
            "Spider Control",
            "Mosquito Control",
            "Wildlife Removal"
        ),
        "Smart Home Installation" to listOf(
            "Smart Thermostat Installation",
            "Smart Lock Installation",
            "Smart Lighting Installation",
            "Smart Security System",
            "Smart Speaker Setup",
            "Smart Home Integration",
            "Smart Blinds Installation",
            "Home Automation Setup",
            "Smart Camera Installation",
            "Smart Appliance Setup"
        ),
        "Outdoor Living" to listOf(
            "Patio Installation",
            "Deck Building",
            "Pergola Installation",
            "Outdoor Kitchen Setup",
            "Fire Pit Installation",
            "Outdoor Furniture Assembly",
            "Gazebo Installation",
            "Outdoor Lighting",
            "Garden Bed Installation",
            "Water Feature Installation"
        ),
        "Garage Services" to listOf(
            "Garage Door Repair",
            "Garage Door Installation",
            "Garage Door Opener Installation",
            "Garage Organization",
            "Epoxy Floor Coating",
            "Garage Storage Solutions",
            "Garage Insulation",
            "Garage Conversion",
            "Garage Cleaning",
            "Garage Door Maintenance"
        ),
        "Furniture Repair" to listOf(
            "Upholstery Repair",
            "Wood Repair",
            "Frame Repair",
            "Reupholstering",
            "Leather Repair",
            "Spring Replacement",
            "Cushion Replacement",
            "Refinishing",
            "Structural Repair",
            "Furniture Restoration"
        ),
        "Heating Services" to listOf(
            "Furnace Repair",
            "Heater Installation",
            "Heater Maintenance",
            "Boiler Repair",
            "Radiant Heating Installation",
            "Heat Pump Repair",
            "Thermostat Installation",
            "HVAC System Maintenance",
            "Heat Exchanger Replacement",
            "Ductwork Repair"
        ),
        "Mold Removal" to listOf(
            "Mold Inspection",
            "Mold Remediation",
            "Mold Removal",
            "Moisture Control",
            "Air Quality Testing",
            "Surface Cleaning",
            "Mold Prevention",
            "Attic Mold Removal",
            "Basement Mold Removal",
            "Structural Mold Repair"
        ),
        "Concrete Services" to listOf(
            "Concrete Pouring",
            "Concrete Repair",
            "Concrete Stamping",
            "Concrete Sealing",
            "Driveway Installation",
            "Patio Installation",
            "Foundation Repair",
            "Sidewalk Installation",
            "Curb Installation",
            "Concrete Resurfacing"
        ),
        "Handyman Services" to listOf(
            "Small Repairs",
            "Furniture Assembly",
            "Home Maintenance",
            "Minor Electrical Work",
            "Minor Plumbing Work",
            "Hanging Pictures",
            "Installing Shelves",
            "Fixing Leaky Faucets",
            "Repairing Drywall",
            "Replacing Light Fixtures"
        ),
        "Interior Design" to listOf(
            "Room Layout Design",
            "Color Schemes",
            "Furniture Selection",
            "Accessory Selection",
            "Lighting Design",
            "Window Treatments",
            "Flooring Selection",
            "Wall Art Selection",
            "Space Planning",
            "Furniture Arrangement"
        ),
        "Window Services" to listOf(
            "Window Installation",
            "Window Repair",
            "Window Replacement",
            "Window Cleaning",
            "Window Tinting",
            "Window Sealing",
            "Storm Window Installation",
            "Window Shading",
            "Skylight Installation",
            "Window Screen Replacement"
        ),
        "Roofing Services" to listOf(
            "Roof Repair",
            "Roof Installation",
            "Roof Inspection",
            "Shingle Replacement",
            "Roof Cleaning",
            "Roof Coating",
            "Gutter Installation",
            "Roof Ventilation",
            "Flashing Repair",
            "Skylight Repair"
        ),
        "Custom Builds" to listOf(
            "Custom Furniture",
            "Built-In Shelving",
            "Custom Cabinets",
            "Home Office Builds",
            "Custom Closets",
            "Entertainment Centers",
            "Custom Countertops",
            "Specialty Storage Solutions",
            "Custom Wall Units",
            "Bespoke Fixtures"
        ),
        "Property Maintenance" to listOf(
            "Routine Maintenance",
            "Emergency Repairs",
            "Inspection Services",
            "Tenant Turnover Services",
            "Preventative Maintenance",
            "Facility Management",
            "Landscaping Maintenance",
            "Building Repairs",
            "Trash Removal",
            "Property Inspections"
        ),
        "Home Security" to listOf(
            "Alarm Systems",
            "Security Cameras",
            "Access Control Systems",
            "Smart Locks",
            "Security Lighting",
            "Surveillance Systems",
            "Motion Sensors",
            "Home Automation",
            "Security Audits",
            "Security System Upgrades"
        ),
        "Energy Efficiency" to listOf(
            "Insulation Installation",
            "Window Sealing",
            "Energy Audits",
            "Solar Panel Installation",
            "HVAC Upgrades",
            "Weatherstripping",
            "Energy-Efficient Lighting",
            "Smart Thermostats",
            "Low-Flow Fixtures",
            "Draft Proofing"
        ),
        "Emergency Services" to listOf(
            "Water Damage Restoration",
            "Fire Damage Repair",
            "Storm Damage Repair",
            "Emergency Plumbing",
            "Emergency Electrical Repairs",
            "Emergency Board-Up Services",
            "Flood Cleanup",
            "Disaster Restoration",
            "Emergency Roof Repairs",
            "Emergency Cleanup Services"
        ),
        "Transportation Services" to listOf(
            "Moving Services",
            "Delivery Services",
            "Transport Rental",
            "Vehicle Towing",
            "Car Maintenance",
            "Taxi Services",
            "Shuttle Services",
            "Cargo Transport",
            "Packing Services",
            "Freight Services"
        )
    )


    try {
        // Reference to the "Categories" collection and the "Services" document
        val documentRef = firestore.collection("Categories").document("Services")

        // Set or update the document with the skills data
        documentRef.set(skillsData).await()

        return "Skills data successfully added to Firestore."
    } catch (e: Exception) {
        // Handle errors
        return "Error adding skills to Firestore: ${e.localizedMessage}"
    }
}*/
