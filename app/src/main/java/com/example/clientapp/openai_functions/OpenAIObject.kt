package com.example.clientapp.openai_functions

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.client.OpenAI
import com.example.clientapp.BuildConfig
import com.example.clientapp.models.AssistantResponse
import com.example.clientapp.openai_functions.OpenAIObject.openAI
import com.example.clientapp.openai_functions.OpenAIObject.thread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.time.Duration.Companion.seconds


@OptIn(BetaOpenAI::class)
object OpenAIObject : CoroutineScope {
    private val job = Job()
    override val coroutineContext = Dispatchers.Default + job

    private const val API_KEY = BuildConfig.OPENAI_API_KEY
    private const val ASSISTANT_ID = BuildConfig.ASSISTANT_ID
    private val openAI = OpenAI(token = API_KEY, timeout = Timeout(socket = 60.seconds))

    private var assistant: Assistant? = null
    private var thread: Thread? = null
    private lateinit var run: Run


    private suspend fun initializeAssistantAndThread() {
        if (assistant == null) {
            assistant = retrieveAssistant()
        }
        if (thread == null) {
            thread = createThread()
        }
    }

    private suspend fun retrieveAssistant(): Assistant? =
        openAI.assistant(id = AssistantId(ASSISTANT_ID)).also {
            if (it != null) {
                Log.d("OpenAIObject", "Assistant retrieved: ${it.id}")
            }
        }

    private suspend fun createThread(): Thread {
        return openAI.thread().also { t ->
            thread = t
            Log.d("OpenAIObject", "Thread created: ${t.id}")
        }
    }

    suspend fun deleteThread(): Boolean = openAI.delete(id = ThreadId(thread?.id.toString()))

    private suspend fun addMessageToThread(prompt: String) {
        openAI.message(
            threadId = thread!!.id,  // Using '!!' because it should be non-null after initialization
            request = MessageRequest(
                role = Role.User,
                content = prompt,
            )
        )

        Log.d("OpenAIObject", "Message added to thread ${thread!!.id}. Message: $prompt")
    }

    private suspend fun createRun() {
        run = openAI.createRun(
            threadId = thread!!.id,
            request = RunRequest(assistantId = AssistantId(ASSISTANT_ID)),
        )

        Log.d("OpenAIObject", "Run created: ${run.id}")
    }



    suspend fun getResponse(): AssistantResponse {
        do {
            Log.d("OpenAIObject", "in getResponse")
            delay(1500) // Adjust the delay as needed

            val retrievedRun = openAI.getRun(threadId = thread!!.id, runId = run.id)
        } while (retrievedRun.status != Status.Completed)

        val runSteps = openAI.runSteps(threadId = run.threadId, runId = run.id)
        Log.d("OpenAIObject", "Run steps: ${runSteps.size}")

        val assistantMessages = openAI.messages(thread!!.id)
        Log.d("OpenAIObject", "Response Size: ${assistantMessages.size}")

        // Find the latest message from the Assistant
        val latestMessage = assistantMessages.lastOrNull { it.role == Role.Assistant }
            ?: error("No message from the assistant found")

        val textContent =
            latestMessage.content.first() as MessageContent.Text //?: error("Expected MessageContent.Text")

        Log.d("OpenAIObject", "Assistant Message: ${textContent.text.value}")

        // TODO maybe take this into a separate function and just return latestMessage?
        // Parse the assistant's response
        val jsonResponse = JSONObject(textContent.text.value)
        val response = jsonResponse.getString("response")
        val inferredServices = jsonResponse.getJSONArray("inferred_services")

        // Convert the inferredServices JSONArray to a Kotlin List<String>
        val servicesList = mutableListOf<String>()

        for (i in 0 until inferredServices.length()) {
            servicesList.add(inferredServices.getString(i))
        }

        return AssistantResponse(
            response = response,
            inferredServices = servicesList
        )
    }



    suspend fun prepareAssistant(prompt: String) {
        Log.d("OpenAIObject", "Preparing assistant")
        initializeAssistantAndThread()  // Ensure assistant and thread are initialized
        addMessageToThread(prompt = prompt)
        createRun()
    }


    fun clear() {
        job.cancel() // Cancel the coroutine when the object is no longer needed

        Log.d("OpenAIObject", "OpenAIObject cleared")
    }
}


/*suspend fun getMessages(): List<Message> {
    do {
        Log.d("OpenAIObject", "in getResponse")
        delay(1500) // Adjust the delay as needed

        val retrievedRun = openAI.getRun(threadId = thread!!.id, runId = OpenAIObject.run.id)
    } while (retrievedRun.status != Status.Completed)

    val runSteps = openAI.runSteps(threadId = OpenAIObject.run.threadId, runId = OpenAIObject.run.id)
    Log.d("OpenAIObject", "Run steps: ${runSteps.size}")

    val assistantMessages = openAI.messages(thread!!.id)
    Log.d("OpenAIObject", "Response Size: ${assistantMessages.size}")

    return assistantMessages.map {
        val textContent =
            it.content.first() as? MessageContent.Text ?: error("Expected MessageContent.Text")

        if (it.role == Role.Assistant) {
            Log.d("OpenAIObject", "Assistant Message: ${textContent.text.value}")
        } else {
            Log.d("OpenAIObject", "User Message: ${textContent.text.value}")
        }

        it
    }
}*/


