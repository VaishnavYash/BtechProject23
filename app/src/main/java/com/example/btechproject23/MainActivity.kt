package com.example.btechproject23

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

private const val xRange: Int = 1000
private const val yRange: Int = 1400

class MainActivity : AppCompatActivity() {
    private var firstBtn: Button? = null
    private var secondBtn: Button? = null
    private var thirdBtn: Button? = null
    private var forthBtn: Button? = null
    private var fifthBtn: Button? = null
    private var sixthBtn: Button? = null
    private var seventhBtn: Button? = null
    private var eightBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstBtn = findViewById(R.id.first_btn)
        secondBtn = findViewById(R.id.second_btn)
        thirdBtn = findViewById(R.id.third_btn)
        forthBtn = findViewById(R.id.forth_btn)
        fifthBtn = findViewById(R.id.fifth_btn)
        sixthBtn = findViewById(R.id.sixth_btn)
        seventhBtn = findViewById(R.id.seventh_btn)
        eightBtn = findViewById(R.id.eighth_btn)

        firstBtn?.setOnClickListener {
            setButtonColor(1,0,0,0,0,0,0,0)
            val fragment1 : Fragment = Fragment1()
            val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.graph_fragment, fragment1).commit()
        }
        secondBtn?.setOnClickListener {
            setButtonColor(0,1,0,0,0,0,0,0)
            val fragment2 : Fragment = Fragment2()
            val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.graph_fragment, fragment2).commit()
        }
        thirdBtn?.setOnClickListener {
            setButtonColor(0,0,1,0,0,0,0,0)
            val fragment2 : Fragment = MedianFragment()
            val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.graph_fragment, fragment2).commit()
        }
        forthBtn?.setOnClickListener {
            setButtonColor(0,0,0,1,0,0,0,0)
            Toast.makeText(this, "No Action", Toast.LENGTH_SHORT).show()
        }
        fifthBtn?.setOnClickListener {
            setButtonColor(0,0,0,0,1,0,0,0)
            Toast.makeText(this, "No Action", Toast.LENGTH_SHORT).show()
        }
        sixthBtn?.setOnClickListener {
            setButtonColor(0,0,0,0,0,1,0,0)
            Toast.makeText(this, "No Action", Toast.LENGTH_SHORT).show()
        }
        seventhBtn?.setOnClickListener {
            setButtonColor(0,0,0,0,0,0,1,0)
            Toast.makeText(this, "No Action", Toast.LENGTH_SHORT).show()
        }
        eightBtn?.setOnClickListener {
            setButtonColor(0,0,0,0,0,0,0,1)
            Toast.makeText(this, "No Action", Toast.LENGTH_SHORT).show()
        }
    }

    fun setButtonColor(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int, h: Int) {
        // Reset all buttons to gray
        firstBtn?.setBackgroundColor(Color.GRAY)
        secondBtn?.setBackgroundColor(Color.GRAY)
        thirdBtn?.setBackgroundColor(Color.GRAY)
        forthBtn?.setBackgroundColor(Color.GRAY)
        fifthBtn?.setBackgroundColor(Color.GRAY)
        sixthBtn?.setBackgroundColor(Color.GRAY)
        seventhBtn?.setBackgroundColor(Color.GRAY)
        eightBtn?.setBackgroundColor(Color.GRAY)

        // Set the color of the button based on the condition
        if (a==1) {
            firstBtn?.setBackgroundColor(Color.GREEN)
        } else if (b==1) {
            secondBtn?.setBackgroundColor(Color.GREEN)
        } else if (c==1) {
            thirdBtn?.setBackgroundColor(Color.GREEN)
        } else if (d==1) {
            forthBtn?.setBackgroundColor(Color.GREEN)
        } else if (e==1) {
            fifthBtn?.setBackgroundColor(Color.GREEN)
        } else if (f==1) {
            sixthBtn?.setBackgroundColor(Color.GREEN)
        } else if (g==1) {
            seventhBtn?.setBackgroundColor(Color.GREEN)
        } else if (h==1) {
            eightBtn?.setBackgroundColor(Color.GREEN)
        }
    }

}
