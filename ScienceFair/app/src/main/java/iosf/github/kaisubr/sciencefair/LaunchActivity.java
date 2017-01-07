package iosf.github.kaisubr.sciencefair;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import iosf.github.kaisubr.sciencefair.CustomAdapters.UserPreferences;
import iosf.github.kaisubr.sciencefair.OtherCustomClasses.WarningToast;

public class LaunchActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ImageView shield = (ImageView) findViewById(R.id.launch_shield);
        ImageView phone = (ImageView) findViewById(R.id.launch_phone);
        ImageView ripple = (ImageView) findViewById(R.id.launch_ripple);
        TextView monitorTextView = (TextView) findViewById(R.id.textViewAppTitle);
        TextView didYouKnow = (TextView) findViewById(R.id.textViewDidYouKnow);

        String didYouKnowArrayList[] = {
                "Monitor works by blocking phone numbers that spoofers are known to use.",
                "A spoofed call involves changing a real phone number into a fake one that shows up on your caller ID.",
                "Be wary before giving others private information.",
                "There are about 86.2 million phone scams in the US every month, and Monitor helps prevent that.",
                "A spoofed call that tries to obtain items of value is considered illegal in the United States.",
                "If a spoofed call has the intent to defraud, it is considered illegal in the United States.",
                "A spoofed call that causes harm is considered illegal in the United States.",
                "End any call immediately that you believe is spoofed.",
                "Monitor has the ability to report any spoofed calls.",
                "Anyone can spoof calls with open source software like Asterisk.",
                "More and more people are having access to software that enables them to create spoofed calls.",
                "It's safe to call back to a number that was spoofed; it will redirect to the legitimate owner of the number."
        };

        String randomDidYouKnow = didYouKnowArrayList[new Random().nextInt(didYouKnowArrayList.length)];

        if (didYouKnow != null) {
            didYouKnow.setText(randomDidYouKnow);
        }

        UserPreferences launchUP = new UserPreferences();
        launchUP.setContext(LaunchActivity.this);

        if (launchUP.getFirstTimeUser().equals("true")) {
            AnimatorSet phSet = (AnimatorSet) AnimatorInflater.loadAnimator(LaunchActivity.this, R.animator.fade_in);
            phSet.setStartDelay(0);
            phSet.setTarget(phone);
            phSet.start();

            AnimatorSet ringing = (AnimatorSet) AnimatorInflater.loadAnimator(LaunchActivity.this, R.animator.ringing);
            ringing.setStartDelay(phSet.getDuration() + 50);
            ringing.setTarget(phone);
            ringing.start();
            final List<Integer> repeatCount = new ArrayList<>();
            repeatCount.add(0);

            ringing.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    repeatCount.add(repeatCount.get(repeatCount.size()-1) + 1);
                    Log.d("Launch", repeatCount.toString());
                    if (repeatCount.get(repeatCount.size()-1) < 10) {
                        animation.start();
                    } else {
                        animation.end();
                    }
                }
            });

            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(LaunchActivity.this, R.animator.fade_in_large);
            set.setTarget(shield);
            set.setStartDelay(2000);
            set.setInterpolator(new FastOutLinearInInterpolator());
            set.start();

            AnimatorSet rippleSet = (AnimatorSet) AnimatorInflater.loadAnimator(LaunchActivity.this, R.animator.ripple);
            rippleSet.setTarget(ripple);
            rippleSet.setStartDelay(3750);
            rippleSet.setInterpolator(new FastOutSlowInInterpolator());
            rippleSet.start();

            AnimatorSet textAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(LaunchActivity.this, R.animator.fade_in);
            textAnimation.setTarget(monitorTextView);
            textAnimation.setStartDelay(4500);
            textAnimation.setInterpolator(new FastOutLinearInInterpolator());
            textAnimation.start();

            Animation textSlide = AnimationUtils.loadAnimation(LaunchActivity.this, R.anim.slide_up);
            monitorTextView.startAnimation(textSlide);

            textSlide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    UserPreferences up = new UserPreferences();
                    up.setContext(LaunchActivity.this);

                    String isFirstTimeUser = up.getFirstTimeUser();

                    startActivity(new Intent(LaunchActivity.this, OnboardingActivity.class));

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            AnimatorSet phoneAnim = (AnimatorSet) AnimatorInflater.loadAnimator(LaunchActivity.this, R.animator.fade_in);
            phoneAnim.setStartDelay(0);
            phoneAnim.setTarget(phone);
            phoneAnim.start();

            AnimatorSet shieldAnim = (AnimatorSet) AnimatorInflater.loadAnimator(LaunchActivity.this, R.animator.fade_in);
            shieldAnim.setStartDelay(100);
            shieldAnim.setTarget(shield);
            shieldAnim.start();

            Log.d("Launch Activity", "Returning user!");

            shieldAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


        }
    }
}
