package course.labs.gestureslab.test

import course.labs.gestureslab.BubbleActivity

import com.robotium.solo.*
import android.test.ActivityInstrumentationTestCase2
import android.view.WindowManager
import junit.framework.Assert

class BubbleActivityFling : ActivityInstrumentationTestCase2<BubbleActivity>(BubbleActivity::class.java) {
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

        solo!!.clickOnActionBarItem(course.labs.gestureslab.R.id.menu_still_mode)

        solo!!.sleep(delay)

        // Click to create a bubble
        solo!!.clickOnScreen(120f, 120f)

        solo!!.sleep(delay)

        // Assert that a bubble was displayed
        Assert.assertEquals(
                "Bubble hasn't appeared",
                1,
                solo!!.getCurrentViews(
                        course.labs.gestureslab.BubbleActivity.BubbleView::class.java)
                        .size)

        // Fling the bubble
        solo!!.drag(100f, 1000f, 100f, 1000f, 3)

        // Give bubble time to leave screen
        solo!!.sleep(delay)

        // Assert that the bubble has left the screen
        Assert.assertEquals(
                "Bubble hasn't left the screen",
                0,
                solo!!.getCurrentViews(
                        course.labs.gestureslab.BubbleActivity.BubbleView::class.java)
                        .size)
    }
}
