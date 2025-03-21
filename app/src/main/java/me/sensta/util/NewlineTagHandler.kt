package me.sensta.util

import android.text.Editable
import android.text.Html
import org.xml.sax.XMLReader

class NewlineTagHandler : Html.TagHandler {
    override fun handleTag(
        opening: Boolean,
        tag: String?,
        output: Editable?,
        xmlReader: XMLReader?
    ) {
        // <br/> 태그를 만났을 때 개행 문자를 추가해주기
        if (tag.equals("br", ignoreCase = true)) {
            output?.append("\n")
        }
    }
}