package com.example.mukana

import java.util.Date
import android.graphics.Color
import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.OnCreateLayout
import com.facebook.litho.annotations.LayoutSpec
import com.facebook.litho.annotations.Prop
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaEdge

// NOTE: the 'Spec' is a keyword in Litho, so it should never be altered or deleted!
@LayoutSpec
object ObsListItemSpec {

    private const val NOTE_STRING_LENGTH = 50
    private const val GEOLOC_DECIMAL_LENGTH = 20
    private const val HEADLINE_TEXT_SIZE = 40f
    private const val NORMAL_TEXT_SIZE = 20f

    // called 'create' when calling it (Litho logic)
    @OnCreateLayout
    internal fun onCreateLayout(
        c: ComponentContext,
        @Prop obs: Observation
    ): Component {

        val bgColor = when (obs.rarity) {
            Rarity.COMMON -> Color.GREEN
            Rarity.RARE -> Color.YELLOW
            Rarity.EXTREMELY_RARE -> Color.MAGENTA
        }

        // limit its decimals a bit
        val formattedGeoLocString = "huu" /* "${obs.geoLocation.latitude.toString().substring(0..GEOLOC_DECIMAL_LENGTH)}," +
                obs.geoLocation.longitude.toString().substring(0..GEOLOC_DECIMAL_LENGTH) */

        // the child text views could be abstracted away into their own component file,
        // but it's easy enough to do if the app gets more complex. rn this is fine.
        return Column.create(c)
            .paddingDip(YogaEdge.ALL, 16f) // Litho uses Yoga under the hood and it takes the enum value from there
            .backgroundColor(bgColor)
            .child(
                Text.create(c)
                    .text("${obs.species}: ${obs.rarity.text}")
                    .backgroundColor(Color.YELLOW)
                    .textSizeSp(HEADLINE_TEXT_SIZE)
            )
            .child(
                Text.create(c)
                    .text("haa")
                    .backgroundColor(Color.BLUE)
                    .textSizeSp(NORMAL_TEXT_SIZE)
            )
            .child(
                Text.create(c)
                    .text(Date(obs.timeStamp.time).toString())
                    .textSizeSp(NORMAL_TEXT_SIZE)
            )
            .child(
                Text.create(c)
                    .text(formattedGeoLocString)
                    .textSizeSp(NORMAL_TEXT_SIZE)
            )
            .build()
    } // onCreateLayout

} // ListItemSpec