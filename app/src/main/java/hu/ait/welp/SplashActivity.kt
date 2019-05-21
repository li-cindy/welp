package hu.ait.welp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH : Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var iconAnim = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.icon_anim)
        ivWIcon.startAnimation(iconAnim)

        Handler().postDelayed({
            var mainIntent = Intent()
            mainIntent.setClass(this@SplashActivity,
                LoginActivity::class.java)

            startActivity(mainIntent)
            this@SplashActivity.finish()
        }, SPLASH_DISPLAY_LENGTH)

    }
}
