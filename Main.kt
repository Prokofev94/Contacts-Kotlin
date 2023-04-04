package contacts

import java.time.Instant

private val phoneBook = mutableListOf<Contact>()

class ContactsApp {
    init { menu() }

    private fun menu() {
        while (true) {
            when (read("Enter action (add, list, search, count, exit)")) {
                "add" -> add()
                "list" -> list()
                "search" -> search()
                "count" -> count()
                "exit"-> return
            }
            println()
        }
    }

    private fun add() {
        val contact = when(read("Enter the type (person, organization)")) {
            "person" -> addPerson()
            else -> addOrganization()
        }
        phoneBook += contact
        println("The record added.")
    }

    private fun addPerson(): Contact {
        val name = read("Enter the name")
        val surname = read("Enter the surname")
        var birthDate = read("Enter the birth date")
        if (birthDate.isEmpty()) {
            println("Bad birth date!")
            birthDate = "[no data]"
        }
        var gender = read("Enter the gender (M, F)")
        if (gender != "M" && gender != "F") {
            println("Bad gender!")
            gender = "[no data]"
        }
        val number = read("Enter the number")
        return PersonalContact(name, surname, birthDate, gender, number)
    }

    private fun addOrganization() =
        OrganizationContact(read("Enter the organization name"), read("Enter the address"), read("Enter the number"))

    private fun list() {
        for (i in phoneBook.indices) {
            println("${i + 1}. ${phoneBook[i]}")
        }
        println()
        val index = read("Enter action ([number], back)")
        phoneBook[index.toInt() - 1].action()
    }

    private fun search() {
        println("Enter search query:")
        val query = read("Enter search query")
        val regex = "(?i).*$query.*".toRegex()
        val result: List<Contact> = phoneBook.filter { it.matches(regex) }
        if (result.isEmpty()) {
            println("Not found")
            return
        } else if (result.size == 1) {
            println("Found 1 result:")
        } else {
            println("Found ${result.size} results")
        }
        for (i in 1..result.size) {
            println("$i. ${result[i - 1]}")
        }
        println()
        when (val input = read("Enter action ([number], back, again)")) {
            "back" -> return
            "again" -> search()
            else -> result[input.toInt() - 1].action()
        }
    }

    private fun count() = println("The Phone Book has ${phoneBook.size} records.")
}

interface Contact {
    var name: String
    var number: String
    val timeCreated: Instant
    var timeLastEdit: Instant

    fun action() {
        info()
        println()
        when (read("Enter action (edit, delete, menu)")) {
            "edit" -> {
                edit()
                timeLastEdit = Instant.now()
                println("Saved")
                info()
                println()
                action()
            }
            "delete" -> remove()
            "menu" -> return
        }
    }

    fun info()

    fun edit()

    fun remove() {
        phoneBook.remove(this)
        println("The record removed!\n")
    }

    fun matches(regex: Regex): Boolean
}

data class PersonalContact(
    override var name: String,
    var surname: String,
    var birthDate: String,
    var gender: String,
    override var number: String
) : Contact {

    override val timeCreated: Instant = Instant.now()
    override var timeLastEdit: Instant = timeCreated

    override fun info() = println(
        """Name: $name
            Surname: $surname
            Birth date: $birthDate
            Gender: $gender
            Number: $number
            Time created: $timeCreated
            Time last edit: $timeLastEdit
            """.trimMargin()
    )

    override fun edit() {
        println("Select a field (name, surname, birth, gender, number):")
        when (readln()) {
            "gender" -> gender = read("Enter gender")
            "birth" -> birthDate = read("Enter birth")
            "number" -> number = read("Enter number")
            "surname" -> surname = read("Enter surname")
            "name" -> name = read("Enter name")
        }
    }

    override fun matches(regex: Regex) = regex.matches(name + surname + number)

    override fun toString() = "$name $surname"
}

data class OrganizationContact(
    override var name: String,
    var address: String,
    override var number: String
) : Contact {

    override val timeCreated: Instant = Instant.now()
    override var timeLastEdit: Instant = timeCreated

    override fun info() = println(
        """Organization name: $name
            Address: $address
            Number: $number
            Time created: $timeCreated
            Time last edit: $timeLastEdit
            """.trimMargin()
    )

    override fun edit() {
        println("Select a field (name, address, number):")
        when (readln()) {
            "name" -> name = read("Enter name")
            "address" -> address = read("Enter address")
            "number" -> number = read("Enter number")
        }
    }

    override fun matches(regex: Regex) = regex.matches(name + address + number)

    override fun toString() = name
}

fun read(message: String) = print("$message: ").run { readln() }

fun main() {
    ContactsApp()
}