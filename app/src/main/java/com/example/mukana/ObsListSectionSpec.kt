package com.example.mukana

import android.location.Location
import com.facebook.litho.sections.common.SingleComponentSection
import com.facebook.litho.sections.Children
import com.facebook.litho.sections.SectionContext
import com.facebook.litho.sections.annotations.OnCreateChildren
import com.facebook.litho.sections.annotations.GroupSectionSpec
import java.sql.Timestamp

// NOTE: the 'Spec' is a keyword in Litho, so it should never be altered or deleted!
@GroupSectionSpec
object ObsListSectionSpec {

    // called 'create' when calling it (Litho logic)
    @OnCreateChildren
    internal fun onCreateChildren(c: SectionContext): Children {

        val builder = Children.create()

        val obs = Observation("lintu", Rarity.COMMON, "hieno", Location(""), Timestamp(System.currentTimeMillis()))

        for (i in 0..31) {
            builder.child(
                SingleComponentSection.create(c)
                    .key(i.toString())
                    .component(ObsListItem.create(c)
                        .obs(obs)
                        .build())
            )
        } // for
        return builder.build()
    } // onCreateChildren

} // ObsListSectionSpec