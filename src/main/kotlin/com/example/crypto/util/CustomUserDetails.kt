package com.example.crypto.util


import com.example.crypto.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class CustomUserDetails(private val user: User) : UserDetails {

    val id: Long
        get() = user.id
    val email: String
        get() = user.email

    override fun getPassword(): String {
        return user.password!!
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {

        return mutableListOf()
    }


    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}