// SPDX-License-Identifier: GPL-3.0-or-later
package io.github.giuclab.recurio
import org.junit.Test
import org.junit.Assert.*
class CommunityTest {
 @Test fun guideEndsWithCommunityAsItsSixthPage(){assertEquals(6,guidePages.size);assertEquals("community",guidePages.last())}
 @Test fun linksPointToRealRepositoryAndNoInventedFdroidListing(){assertEquals("https://github.com/GiuClab/Recurio",ProjectLinks.repository);assertEquals("https://github.com/GiuClab/Recurio/releases",ProjectLinks.downloads);assertEquals("https://github.com/GiuClab/Recurio/issues/new/choose",ProjectLinks.feedback);assertEquals("https://ko-fi.com/giuclab/tip",ProjectLinks.support);assertNull(ProjectLinks.fdroid)}
 @Test fun communityAndShareTextAreLocalized(){for(lang in languages.keys)for(key in listOf("guide_community_title","guide_community_body","feedback","comingSoon","shareApp","shareAppHelp","shareLink","copyLink","linkCopied","shareError","sectionCommunity"))assertNotEquals(key,tr(lang,key))}
}
