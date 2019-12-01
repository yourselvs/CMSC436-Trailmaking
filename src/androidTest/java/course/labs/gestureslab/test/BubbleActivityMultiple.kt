package course.labs.gestureslab.test

import course.labs.gestureslab.BubbleActivity

import com.robotium.solo.*
import android.test.ActivityInstrumentationTestCase2
import android.view.WindowManager
import junit.framework.Assert

class BubbleActivityMultiple : ActivityInstrumentationTestCase2<BubbleActivity>(BubbleActivity::class.java) {
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
        solo!!.waitForActivity(course.labs.gestureslab.BubbleActivity::class.java,
                delay)

        // Set Still Mode
        solo!!.clickOnActionBarItem(course.labs.gestureslab.R.id.menu_still_mode)

        solo!!.sleep(delay)

        // Click to create a bubble
        solo!!.clickOnScreen(100f, 100f)

        solo!!.sleep(delay)

        // Assert that a bubble was displayed
        Assert.assertEquals(
                "Bubble hasn't appeared",
                1,
                solo!!.getCurrentViews<course.labs.gestureslab.BubbleActivity.BubbleView>(
                        course.labs.gestureslab.BubbleActivity.BubbleView::class.java)
                        .size)

        // Click to create second bubble
        solo!!.clickOnScreen(300f, 300f)

        solo!!.sleep(delay)

        // Assert that a bubble was displayed
        Assert.assertEquals(
                "Second bubble hasn't appeared",
                2,
                solo!!.getCurrentViews<course.labs.gestureslab.BubbleActivity.BubbleView>(
                        course.labs.gestureslab.BubbleActivity.BubbleView::class.java)
                        .size)

        solo!!.sleep(delay)

        // Give misbehaving bubbles a chance to move off screen
        // Assert that there are two bubbles on the screen
        Assert.assertEquals(
                "There should be two bubbles on the screen",
                2,
                solo!!.getCurrentViews<course.labs.gestureslab.BubbleActivity.BubbleView>(
                        course.labs.gestureslab.BubbleActivity.BubbleView::class.java)
                        .size)
    }
}
