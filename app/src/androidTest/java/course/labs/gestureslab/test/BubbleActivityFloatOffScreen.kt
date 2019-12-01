package course.labs.gestureslab.test

import course.labs.gestureslab.BubbleActivity

import com.robotium.solo.*
import android.test.ActivityInstrumentationTestCase2
import android.view.WindowManager
import junit.framework.Assert

class BubbleActivityFloatOffScreen : ActivityInstrumentationTestCase2<BubbleActivity>(BubbleActivity::class.java) {
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

        val shortDelay = 300
        val delay = 2000
        val delay2 = 3000

        // Wait for activity: 'course.labs.TouchLab.BubbleActivity'
        solo!!.waitForActivity(course.labs.gestureslab.BubbleActivity::class.java,
                delay)

        // Click on action bar item
        solo!!.clickOnActionBarItem(course.labs.gestureslab.R.id.menu_single_speed)

        solo!!.sleep(delay)

        // Click to create a bubble
        solo!!.clickOnScreen(350.0f, 350.0f)

        // Check whether bubble appears
        var bubbleAppeared = solo!!.getCurrentViews<course.labs.gestureslab.BubbleActivity.BubbleView>(
                course.labs.gestureslab.BubbleActivity.BubbleView::class.java).size > 0
        var i = 0
        while (i < 10 && !bubbleAppeared) {
            solo!!.sleep(shortDelay)
            bubbleAppeared = solo!!.getCurrentViews<course.labs.gestureslab.BubbleActivity.BubbleView>(
                    course.labs.gestureslab.BubbleActivity.BubbleView::class.java)
                    .size > 0
            i++
        }

        // Assert that a bubble was displayed
        Assert.assertTrue("Bubble hasn't appeared", bubbleAppeared)

        solo!!.sleep(delay2)

        // Assert that the bubble has left the screen
        Assert.assertEquals(
                "Bubble hasn't left the screen",
                0,
                solo!!.getCurrentViews<course.labs.gestureslab.BubbleActivity.BubbleView>(
                        course.labs.gestureslab.BubbleActivity.BubbleView::class.java)
                        .size)

    }
}
