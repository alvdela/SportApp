package com.alvdela.sportapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.alvdela.sportapp.Utility.animateViewOfFloat
import com.alvdela.sportapp.Utility.animateViewOfInt
import com.alvdela.sportapp.Utility.getFormattedStopWatch
import com.alvdela.sportapp.Utility.getSecondsFromWatch
import com.alvdela.sportapp.Utility.setHeightLinearLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import me.tankery.lib.circularseekbar.CircularSeekBar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var widthScreenPixels: Int = 0
    private var heightScreenPixels: Int = 0
    private var widthAnimations: Int = 0

    private lateinit var drawer: DrawerLayout

    private lateinit var csbChallengeDistance: CircularSeekBar
    private lateinit var csbCurrentDistance: CircularSeekBar
    private lateinit var csbRecordDistance: CircularSeekBar

    private lateinit var csbCurrentAvgSpeed: CircularSeekBar
    private lateinit var csbRecordAvgSpeed: CircularSeekBar

    private lateinit var csbCurrentSpeed: CircularSeekBar
    private lateinit var csbCurrentMaxSpeed: CircularSeekBar
    private lateinit var csbRecordSpeed: CircularSeekBar

    private lateinit var tvDistanceRecord: TextView
    private lateinit var tvAvgSpeedRecord: TextView
    private lateinit var tvMaxSpeedRecord: TextView


    private lateinit var swIntervalMode: Switch
    private lateinit var swChallenges: Switch
    private lateinit var swVolumes: Switch

    private lateinit var npChallengeDistance: NumberPicker
    private lateinit var npChallengeDurationHH: NumberPicker
    private lateinit var npChallengeDurationMM: NumberPicker
    private lateinit var npChallengeDurationSS: NumberPicker

    private var challengeDistance: Float = 0f
    private var challengeDuration: Int = 0

    private lateinit var tvChrono: TextView

    private lateinit var npDurationInterval: NumberPicker
    private lateinit var tvRunningTime: TextView
    private lateinit var tvWalkingTime: TextView
    private lateinit var csbRunWalk: CircularSeekBar

    private var ROUND_INTERVAL = 300
    private var TIME_RUNNING: Int = 0

    private lateinit var lyPopupRun: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initObjects()
        initToolBar()
        initNavView()
    }

    private fun initStopWatch() {
        tvChrono.text = getString(R.string.init_stop_watch_value)
    }
    private fun initChrono(){
        tvChrono = findViewById(R.id.tvChrono)
        tvChrono.setTextColor(ContextCompat.getColor( this, R.color.white))
        initStopWatch()

        widthScreenPixels = resources.displayMetrics.widthPixels
        heightScreenPixels = resources.displayMetrics.heightPixels

        widthAnimations = widthScreenPixels


        val lyChronoProgressBg = findViewById<LinearLayout>(R.id.lyChronoProgressBg)
        val lyRoundProgressBg = findViewById<LinearLayout>(R.id.lyRoundProgressBg)
        lyChronoProgressBg.translationX = -widthAnimations.toFloat()
        lyRoundProgressBg.translationX = -widthAnimations.toFloat()
    }
    private fun hideLayouts(){
        var lyMap = findViewById<LinearLayout>(R.id.lyMap)
        var lyFragmentMap = findViewById<LinearLayout>(R.id.lyFragmentMap)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyChallengesSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenges = findViewById<LinearLayout>(R.id.lyChallenges)
        val lySettingsVolumesSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolumes = findViewById<LinearLayout>(R.id.lySettingsVolumes)
        var lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        var lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)


        setHeightLinearLayout(lyMap, 0)
        setHeightLinearLayout(lyIntervalModeSpace,0)
        setHeightLinearLayout(lyChallengesSpace,0)
        setHeightLinearLayout(lySettingsVolumesSpace,0)
        setHeightLinearLayout(lySoftTrack,0)
        setHeightLinearLayout(lySoftVolume,0)

        lyFragmentMap.translationY = -300f
        lyIntervalMode.translationY = -300f
        lyChallenges.translationY = -300f
        lySettingsVolumes.translationY = -300f
    }
    private fun initMetrics(){
        csbCurrentDistance = findViewById(R.id.csbCurrentDistance)
        csbChallengeDistance = findViewById(R.id.csbChallengeDistance)
        csbRecordDistance = findViewById(R.id.csbRecordDistance)

        csbCurrentAvgSpeed = findViewById(R.id.csbCurrentAvgSpeed)
        csbRecordAvgSpeed = findViewById(R.id.csbRecordAvgSpeed)

        csbCurrentSpeed = findViewById(R.id.csbCurrentSpeed)
        csbCurrentMaxSpeed = findViewById(R.id.csbCurrentMaxSpeed)
        csbRecordSpeed = findViewById(R.id.csbRecordSpeed)

        csbCurrentDistance.progress = 0f
        csbChallengeDistance.progress = 0f

        csbCurrentAvgSpeed.progress = 0f

        csbCurrentSpeed.progress = 0f
        csbCurrentMaxSpeed.progress = 0f

        tvDistanceRecord = findViewById(R.id.tvDistanceRecord)
        tvAvgSpeedRecord = findViewById(R.id.tvAvgSpeedRecord)
        tvMaxSpeedRecord = findViewById(R.id.tvMaxSpeedRecord)

        tvDistanceRecord.text = ""
        tvAvgSpeedRecord.text = ""
        tvMaxSpeedRecord.text = ""
    }
    private fun initSwitchs(){
        swIntervalMode = findViewById(R.id.swIntervalMode)
        swChallenges = findViewById(R.id.swChallenges)
        swVolumes = findViewById(R.id.swVolumes)
    }
    private fun initIntervalMode(){
        npDurationInterval = findViewById(R.id.npDurationInterval)
        tvRunningTime = findViewById(R.id.tvRunningTime)
        tvWalkingTime = findViewById(R.id.tvWalkingTime)
        csbRunWalk = findViewById<CircularSeekBar>(R.id.csbRunWalk)

        npDurationInterval.minValue = 1
        npDurationInterval.maxValue = 60
        npDurationInterval.value = 5
        npDurationInterval.wrapSelectorWheel = true
        npDurationInterval.setFormatter { i -> String.format("%02d", i) }

        npDurationInterval.setOnValueChangedListener { _, _, newVal ->
            csbRunWalk.max = (newVal * 60).toFloat()
            val maximo = csbRunWalk.max
            csbRunWalk.progress = maximo/2

            tvRunningTime.text = getFormattedStopWatch(((newVal*60/2)*1000).toLong()).subSequence(3,8)
            tvWalkingTime.text = tvRunningTime.text

            ROUND_INTERVAL = newVal * 60
            TIME_RUNNING = ROUND_INTERVAL / 2
        }

        csbRunWalk.max = 300f
        csbRunWalk.progress = 150f
        csbRunWalk.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Float,
                fromUser: Boolean
            ) {

                if (fromUser){
                    var STEPS_UX: Int = 15
                    if (ROUND_INTERVAL > 600) STEPS_UX = 60
                    if (ROUND_INTERVAL > 1800) STEPS_UX = 300
                    var set: Int = 0
                    var p = progress.toInt()

                    var limit = 60
                    if (ROUND_INTERVAL > 1800) limit = 300

                    if (p%STEPS_UX != 0 && progress != csbRunWalk.max){
                        while (p >= limit) p -= limit
                        while (p >= STEPS_UX) p -= STEPS_UX
                        if (STEPS_UX-p > STEPS_UX/2) set = -1 * p
                        else set = STEPS_UX-p

                        if (csbRunWalk.progress + set > csbRunWalk.max)
                            csbRunWalk.progress = csbRunWalk.max
                        else
                            csbRunWalk.progress += set
                    }
                }

                tvRunningTime.text = getFormattedStopWatch((csbRunWalk.progress.toInt() *1000).toLong()).subSequence(3,8)
                tvWalkingTime.text = getFormattedStopWatch(((ROUND_INTERVAL- csbRunWalk.progress.toInt())*1000).toLong()).subSequence(3,8)
                TIME_RUNNING = getSecondsFromWatch(tvRunningTime.text.toString())
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            }
        })
    }
    private fun initChallengeMode(){
        npChallengeDistance = findViewById(R.id.npChallengeDistance)
        npChallengeDurationHH = findViewById(R.id.npChallengeDurationHH)
        npChallengeDurationMM = findViewById(R.id.npChallengeDurationMM)
        npChallengeDurationSS = findViewById(R.id.npChallengeDurationSS)

        npChallengeDistance.minValue = 1
        npChallengeDistance.maxValue = 300
        npChallengeDistance.value = 10
        npChallengeDistance.wrapSelectorWheel = true


        npChallengeDistance.setOnValueChangedListener { picker, oldVal, newVal ->
            challengeDistance = newVal.toFloat()
            csbChallengeDistance.max = newVal.toFloat()
            csbChallengeDistance.progress = newVal.toFloat()
            challengeDuration = 0

            if (csbChallengeDistance.max > csbRecordDistance.max)
                csbCurrentDistance.max = csbChallengeDistance.max
        }

        npChallengeDurationHH.minValue = 0
        npChallengeDurationHH.maxValue = 23
        npChallengeDurationHH.value = 1
        npChallengeDurationHH.wrapSelectorWheel = true
        npChallengeDurationHH.setFormatter { i -> String.format("%02d", i) }

        npChallengeDurationMM.minValue = 0
        npChallengeDurationMM.maxValue = 59
        npChallengeDurationMM.value = 0
        npChallengeDurationMM.wrapSelectorWheel = true
        npChallengeDurationMM.setFormatter { i -> String.format("%02d", i) }

        npChallengeDurationSS.minValue = 0
        npChallengeDurationSS.maxValue = 59
        npChallengeDurationSS.value = 0
        npChallengeDurationSS.wrapSelectorWheel = true
        npChallengeDurationSS.setFormatter { i -> String.format("%02d", i) }

        npChallengeDurationHH.setOnValueChangedListener { _, _, newVal ->
            getChallengeDuration(newVal, npChallengeDurationMM.value, npChallengeDurationSS.value)
        }
        npChallengeDurationMM.setOnValueChangedListener { _, _, newVal ->
            getChallengeDuration(npChallengeDurationHH.value, newVal, npChallengeDurationSS.value)
        }
        npChallengeDurationSS.setOnValueChangedListener { _, _, newVal ->
            getChallengeDuration(npChallengeDurationHH.value, npChallengeDurationMM.value, newVal)
        }

    }
    private fun hidePopUpRun(){
        val lyWindow = findViewById<LinearLayout>(R.id.lyWindow)
        lyWindow.translationX = 400f
        lyPopupRun = findViewById(R.id.lyPopupRun)
        lyPopupRun.isVisible = false
    }
    private fun initObjects(){
        initChrono()
        hideLayouts()
        initMetrics()
        initSwitchs()
        initIntervalMode()
        initChallengeMode()
        hidePopUpRun()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initNavView() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val headerView: View =
            LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false)
        navigationView.removeHeaderView(headerView)
        navigationView.addHeaderView(headerView)

        /*val tvUser: TextView = findViewById(R.id.tvUser)
        tvUser.text = userEmail*/
    }

    private fun initToolBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title_terms,
            R.string.navigation_drawer_close
        )

        drawer.addDrawerListener(toggle)

        toggle.syncState()
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_item_record -> callRecordActivity()
            R.id.nav_item_signout -> signOut()
        }

        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun callRecordActivity() {
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }

    fun changeTypeMap(view: View) {}
    fun callCenterMap(view: View) {}
    fun callShowHideMap(view: View) {}
    fun startOrStopButtonClicked(view: View) {}

    fun takePicture(view: View) {}

    fun selectBike(view: View) {}
    fun selectRollerSkate(view: View) {}
    fun selectRunning(view: View) {}

    fun inflateIntervalMode(view: View) {
        val lyIntervalMode = findViewById<LinearLayout>(R.id.lyIntervalMode)
        val lyIntervalModeSpace = findViewById<LinearLayout>(R.id.lyIntervalModeSpace)
        val lySoftTrack = findViewById<LinearLayout>(R.id.lySoftTrack)
        val lySoftVolume = findViewById<LinearLayout>(R.id.lySoftVolume)
        val tvRounds = findViewById<TextView>(R.id.tvRounds)
        val swIntervalMode = findViewById<Switch>(R.id.swIntervalMode)

        if (swIntervalMode.isChecked) {
            animateViewOfInt(
                lyIntervalMode,
                "textColor",
                ContextCompat.getColor(this, R.color.orange),
                500
            )
            setHeightLinearLayout(lyIntervalModeSpace, 550)
            animateViewOfFloat(lyIntervalMode, "translationY", 0f, 500)

            animateViewOfFloat(tvChrono, "translationX", -110f, 500)
            tvRounds.setText(R.string.rounds)
            animateViewOfInt(
                tvRounds,
                "textColor",
                ContextCompat.getColor(this, R.color.white),
                500
            )

            setHeightLinearLayout(lySoftTrack, 120)
            setHeightLinearLayout(lySoftVolume, 200)
            if (swVolumes.isChecked) {
                val lySettingsVolumeSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
                setHeightLinearLayout(lySettingsVolumeSpace, 600)
            }
        } else {
            animateViewOfInt(
                lyIntervalMode,
                "textColor",
                ContextCompat.getColor(this, R.color.white),
                300
            )
            setHeightLinearLayout(lyIntervalModeSpace, 0)
            lyIntervalMode.translationY = -300f

            animateViewOfFloat(tvChrono, "translationX", 0f, 500)
            tvRounds.setText(R.string.textNull)

            setHeightLinearLayout(lySoftTrack, 0)
            setHeightLinearLayout(lySoftVolume, 0)
        }

    }

    fun inflateChallenges(view: View) {
        val lyChallengeSpace = findViewById<LinearLayout>(R.id.lyChallengesSpace)
        val lyChallenge = findViewById<LinearLayout>(R.id.lyChallenges)
        if (swChallenges.isChecked) {
            animateViewOfInt(
                swChallenges,
                "textColor",
                ContextCompat.getColor(this, R.color.orange),
                500
            )
            setHeightLinearLayout(lyChallengeSpace, 750)
            animateViewOfFloat(lyChallenge, "translationY", 0f, 500)
        } else {
            swChallenges.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lyChallengeSpace, 0)
            lyChallenge.translationY = -300f

            challengeDistance = 0f
            challengeDuration = 0
        }
    }

    fun inflateVolumes(view: View) {
        val lySettingsVolumeSpace = findViewById<LinearLayout>(R.id.lySettingsVolumesSpace)
        val lySettingsVolume = findViewById<LinearLayout>(R.id.lySettingsVolumes)

        if (swVolumes.isChecked) {
            animateViewOfInt(
                swVolumes,
                "textColor",
                ContextCompat.getColor(this, R.color.orange),
                500
            )

            val swIntervalMode = findViewById<Switch>(R.id.swIntervalMode)
            var value = 400
            if (swIntervalMode.isChecked) value = 600

            setHeightLinearLayout(lySettingsVolumeSpace, value)
            animateViewOfFloat(lySettingsVolume, "translationY", 0f, 500)
        } else {
            swVolumes.setTextColor(ContextCompat.getColor(this, R.color.white))
            setHeightLinearLayout(lySettingsVolumeSpace, 0)
            lySettingsVolume.translationY = -300f

        }
    }

    fun showDuration(view: View) {
        showChallenge("duration")
    }

    fun showDistance(view: View) {
        showChallenge("distance")
    }

    private fun showChallenge(option: String) {
        val lyChallengeDuration = findViewById<LinearLayout>(R.id.lyChallengeDuration)
        val lyChallengeDistance = findViewById<LinearLayout>(R.id.lyChallengeDistance)
        val tvChallengeDuration = findViewById<TextView>(R.id.tvChallengeDuration)
        val tvChallengeDistance = findViewById<TextView>(R.id.tvChallengeDistance)

        when (option) {
            "duration" -> {
                lyChallengeDuration.translationZ = 5f
                lyChallengeDistance.translationZ = 0f

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this, R.color.orange))
                tvChallengeDuration.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_dark
                    )
                )

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvChallengeDistance.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_medium
                    )
                )

                challengeDistance = 0f
                challengeDuration = getChallengeDuration(
                    npChallengeDurationHH.value,
                    npChallengeDurationMM.value, npChallengeDurationSS.value
                )

            }

            "distance" -> {
                lyChallengeDuration.translationZ = 0f
                lyChallengeDistance.translationZ = 5f

                tvChallengeDistance.setTextColor(ContextCompat.getColor(this, R.color.orange))
                tvChallengeDistance.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_dark
                    )
                )

                tvChallengeDuration.setTextColor(ContextCompat.getColor(this, R.color.white))
                tvChallengeDuration.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_medium
                    )
                )

                challengeDuration = 0
                challengeDistance = npChallengeDistance.value.toFloat()
            }
        }
    }

    private fun getChallengeDuration(hh: Int, mm: Int, ss: Int): Int {
        var hours: String = hh.toString()
        if (hh < 10) hours = "0$hours"
        var minutes: String = mm.toString()
        if (mm < 10) minutes = "0$minutes"
        var seconds: String = ss.toString()
        if (ss < 10) seconds = "0$seconds"

        return getSecondsFromWatch("$hours:$minutes:$seconds")
    }

    fun closePopUp(view: View) {}
    fun deleteRun(view: View) {}
}