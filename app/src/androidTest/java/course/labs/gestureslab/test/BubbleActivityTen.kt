package course.labs.gestureslab.test

import android.test.ActivityInstrumentationTestCase2
import android.view.WindowManager

import com.robotium.solo.Solo

import course.labs.gestureslab.BubbleActivity
import junit.framework.Assert

class BubbleActivityTen : ActivityInstrumentationTestCase2<BubbleActivity>(BubbleActivity::class.java) {
    private var solo: Solo? = null

    @Throws(Exception::class)
    public override fun setUp() {
        solo = Solo(instrumentation, activity)
        instrumentation.runOnMainSync {
            activity.window.addFlags(
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
    }

    @Throws(Exception::class)
    public override fun tearDown() {
        solo!!.finishOpenedActivities()
    }

    fun testRun() {

        val delay = 2000

        // Wait for activity: 'course.labs.TouchLab.BubbleActivity'
        solo!!.waitForActivity(course.labs.gestureslab.BubbleActivity::class.java, 2000)

        // Set Still Mode
        solo!!.clickOnActionBarItem(course.labs.gestureslab.R.id.menu_still_mode)

        solo!!.sleep(delay)

        //Gesture starting in top right to add ten bubbles
        solo!!.drag(300f, 0f, 200f, 300f, 10)

        solo!!.sleep(delay)

        // Give misbehaving bubbles a chance to move off screen
        // Assert that there are two bubbles on the screen
        Assert.assertEquals(
                "There should be ten bubbles on the screen",
                10,
                solo!!.getCurrentViews<course.labs.gestureslab.BubbleActivity.BubbleView>(
                        course.labs.gestureslab.BubbleActivity.BubbleView::class.java)
                        .size)

    }
}
