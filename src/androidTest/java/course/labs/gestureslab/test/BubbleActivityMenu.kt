package course.labs.gestureslab.test

import android.test.ActivityInstrumentationTestCase2
import android.view.WindowManager

import com.robotium.solo.Solo

import course.labs.gestureslab.BubbleActivity
import junit.framework.Assert


class BubbleActivityMenu : ActivityInstrumentationTestCase2<BubbleActivity>(BubbleActivity::class.java) {
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

        solo!!.clickOnActionBarItem(course.labs.gestureslab.R.id.menu_still_mode)

        solo!!.sleep(delay)

        //Gesture starting top left to open menu
        solo!!.drag(0f, 300f, 300f, 350f, 10)

        solo!!.sleep(delay)

        //checking if menu opened by clicking on a menu item
        //without opening the menu.
        Assert.assertTrue("Menu did not appear", solo!!.waitForText("Random Speed Mode"))

    }
}
