package countries

import scalatags.Text.all._


object Page {

  val appId = "app"

  val boot = s"CountriesClient.main(document.getElementById('$appId'))"

  val skeleton =
    html(
      head(
        link(
          rel := "stylesheet",
          href := "https://cdnjs.cloudflare.com/ajax/libs/pure/0.5.0/pure-min.css"
        ),
//        script(
//          src := "/app-jsdeps.js"
//        ),
        script(
          src := "/app-fastopt.js"
        )
      ),
      body(
        onload := boot,
        div(id := appId)
      )
    )

}
