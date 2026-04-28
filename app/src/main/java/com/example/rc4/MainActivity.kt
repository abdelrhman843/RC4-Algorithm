package com.example.rc4

import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.Math.random
import kotlin.math.log
import kotlin.math.pow
import kotlin.properties.Delegates
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var numberOfBits: String
    var n by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyInput = findViewById<TextInputEditText>(R.id.keyInput)
        val keyInputLayout = findViewById<TextInputLayout>(R.id.keyInputLayout)
        val messageInput = findViewById<TextInputEditText>(R.id.messageInput)
        val messageInputLayout = findViewById<TextInputLayout>(R.id.messageInputLayout)
        val generateKeyBtn = findViewById<Button>(R.id.generateKeyBtn)
        val bitsSpinner = findViewById<Spinner>(R.id.bitsSpinner)
        val showResultBtn = findViewById<Button>(R.id.showResultBtn)
        val resultOutput = findViewById<TextView>(R.id.resultOutput)
        val resultLabel = findViewById<TextView>(R.id.resultLabel)



        var s: MutableList<Int> = mutableListOf()


        generateKeyBtn.setOnClickListener {
            var str = ""
            for (i in 0..5){
                str += Random.nextInt(0,10).toString()
            }
            keyInput.setText(str)
        }


        showResultBtn.setOnClickListener {
            val key = keyInput.text.toString()
            if(key.isEmpty()){
                keyInputLayout.error = "Key is required"
                keyInput.requestFocus()
                return@setOnClickListener
            }
            val message = messageInput.text.toString()
            if(message.isEmpty()){
                messageInputLayout.error = "Message is required"
                messageInput.requestFocus()
                return@setOnClickListener
            }
            numberOfBits = bitsSpinner.selectedItem.toString()
            numberOfBits = numberOfBits.substring(0, numberOfBits.length - 4)
            n = 2.0.pow(numberOfBits.toDouble()).toInt() - 1
            s.clear()
            for (i in 0 .. n) {
                s.add(i)
            }
            Log.d("s before ksa", s.toString())
            Log.d("s before ksa", "$n ${key.length}")
            s = KSA(key, s)
            Log.d("s after ksa", s.toString())
            var result = PRGA(s, message)
            var finalResult: MutableList<String> = mutableListOf()
            Log.d("result", result.toString())
            for(i in 0..<result.size){
                if(result[i] in 0..255){
                    finalResult.add(result[i].toChar().toString())
                }else{
                    finalResult.add(result[i].toString())
                }
            }
            var asciiRes = result.toString().substring(1, result.toString().length - 1)
            var alphaRes = finalResult.toString().substring(1, finalResult.toString().length - 1)
            alphaRes = alphaRes.replace(", ", "")

            resultOutput.text = "ASCII: $asciiRes\n\nAlpha: $alphaRes"
            resultOutput.visibility = VISIBLE
            resultLabel.visibility = VISIBLE

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

    fun PRGA(s: MutableList<Int>, message: String): MutableList<Int> {
        var result: MutableList<Int> = mutableListOf()
        var size = message.length
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
            var newP = k.xor(message[counter].code)
//            newP = if(newP in '0'.code .. '9'.code) newP - '0'.code else newP
            result.add(newP)
            counter++
        }
        return result

    }


}