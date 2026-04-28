package com.example.rc4

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.pow
import kotlin.properties.Delegates
import kotlin.random.Random

class DecryptActivity : AppCompatActivity() {
    lateinit var numberOfBits: String
    var n by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt)

        val copyBtn = findViewById<Button>(R.id.copyBtn)
        val resultOutput = findViewById<TextView>(R.id.resultOutput)
        val resultLabel = findViewById<TextView>(R.id.resultLabel)
        val showResultBtn = findViewById<Button>(R.id.showResultBtn)
        val bitsSpinner = findViewById<Spinner>(R.id.bitsSpinner)
        val messageInput = findViewById<TextInputEditText>(R.id.messageInput)
        val keyInput = findViewById<TextInputEditText>(R.id.keyInput)
        val generateKeyBtn = findViewById<Button>(R.id.generateKeyBtn)
        val messageInputLayout = findViewById<TextInputLayout>(R.id.messageInputLayout)
        val keyInputLayout = findViewById<TextInputLayout>(R.id.keyInputLayout)

        var s: MutableList<Int> = mutableListOf()


        generateKeyBtn.setOnClickListener {
            var str = ""
            for (i in 0..5){
                str += Random.nextInt(0,10).toString()
            }
            keyInput.setText(str)
        }

        // Basic copy functionality for the design
        copyBtn.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("Decrypted Message", resultOutput.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
        }


        showResultBtn.setOnClickListener {
            val key = keyInput.text.toString()
            if (key.isEmpty()) {
                keyInputLayout.error = "Key is required"
                keyInput.requestFocus()
                return@setOnClickListener
            }
            var message = messageInput.text.toString()
            if (message.isEmpty()) {
                messageInputLayout.error = "Message is required"
                messageInput.requestFocus()
                return@setOnClickListener
            }
            message = message.replace(" ", "")
            var messageList: MutableList<Int> = mutableListOf()

            var size = message.length
            var i = 0
            while(size != 0){
                size--
                if(message[i] == ','){
                    messageList.add(message.substring(0, i).toInt())
                    message = message.substring(i+1)
                    i = -1
                }
                i++
            }
            messageList.add(message.toInt())

            numberOfBits = bitsSpinner.selectedItem.toString()
            numberOfBits = numberOfBits.substring(0, numberOfBits.length - 4)
            n = 2.0.pow(numberOfBits.toDouble()).toInt() - 1
            s.clear()
            for (i in 0..n) {
                s.add(i)
            }

            s = KSA(key, s)
            var result = PRGA(s, messageList)
            Log.d("check result", "$result")

            var finalResult: MutableList<String> = mutableListOf()
            for(i in 0..<result.size){
                finalResult.add(result[i].toChar().toString())
            }
            var asciiRes = result.toString().substring(1, result.toString().length - 1)
            var alphaRes = finalResult.toString().substring(1, finalResult.toString().length - 1)
            alphaRes = alphaRes.replace(", ", "")
            resultOutput.text = "ASCII: $asciiRes\n\nAlpha: $alphaRes"
            resultOutput.visibility = View.VISIBLE
            resultLabel.visibility = View.VISIBLE
            copyBtn.visibility = View.VISIBLE
        }

    }

    fun KSA(key: String, s: MutableList<Int>): MutableList<Int> {
        var j = 0
        for (i in 0 .. n) {
            var k = key[i % key.length].code
            if(k in '0'.code ..'9'.code){
                k -= '0'.code
            }
//            Log.d("track ", "old j $j , s[i] ${s[i]} , k $k")
            j = (j + s[i] + k) % (n+1)
//            Log.d("track j", "$j")
            var temp = s[i]
            s[i] = s[j]
            s[j] = temp
//            Log.d("track s ", "i $i j $j s: $s")
        }
        return s
    }

    fun PRGA(s: MutableList<Int>, message: MutableList<Int>): MutableList<Int> {
        var result: MutableList<Int> = mutableListOf()
        var size = message.size
        var i = 0
        var j = 0
        var counter = 0

        while(size != 0){
            size--
            i = (i + 1) % (n+1)
            j = (j + s[i]) % (n+1)
            var temp = s[i]
            s[i] = s[j]
            s[j] = temp
            var k = s[(s[i] + s[j]) % (n+1)]
            var newP = k.xor(message[counter])
//            newP = if(newP in '0'.code .. '9'.code) newP - '0'.code else newP
            result.add(newP)
            counter++
        }
        return result
    }
}
