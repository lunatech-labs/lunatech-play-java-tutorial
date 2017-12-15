def test(strings: String*): Option[String] = {
  if(strings.isEmpty)
    Option.empty
  else
    Some(strings.mkString("\n"))
}

test().getOrElse("")

test("s1","s2").get