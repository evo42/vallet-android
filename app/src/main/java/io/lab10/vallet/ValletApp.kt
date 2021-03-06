package io.lab10.vallet

import android.app.Application
import android.widget.Toast
import io.lab10.vallet.models.MyObjectBox
import io.lab10.vallet.models.Token
import io.lab10.vallet.models.Token_
import io.lab10.vallet.models.Wallet
import io.objectbox.BoxStore
import io.objectbox.exception.DbException

open class ValletApp: Application() {
    companion object {
        var box: BoxStore? = null
        var isAdmin: Boolean = false
        var activeToken: Token?
            get() {
                val tokenBox = box!!.boxFor(Token::class.java)
                var at = tokenBox.query().equal(Token_.active, true).build().findFirst()
                // In case if there is no active token (at) we pick first and set it as active token
                if (at == null) {
                    var nat = tokenBox.query().build().findFirst()
                    if (nat != null) {
                        nat!!.active = true
                        tokenBox.put(nat)
                    }
                    return nat
                } else {
                    return at
                }
            }
            set(value) {
                val tokenBox = box!!.boxFor(Token::class.java)
                val allTokens = tokenBox.query().build().find()
                // Reset current active tokens (there should be one but just in case we set for all)
                allTokens.forEach { p ->
                    p.active = false
                }
                tokenBox.put(allTokens)
                val activeToken = tokenBox.query().equal(Token_.id, (value as Token).id).build().findFirst()
                activeToken!!.active = true
                tokenBox.put(activeToken)
            }
        var wallet: Wallet?
            get() {
                val walletBox = box!!.boxFor(Wallet::class.java)
                return walletBox.query().build().findFirst()
            }
            set(value) {
                val walletBox = box!!.boxFor(Wallet::class.java)
                // TODO add support for multiple wallets
                walletBox.put((value as Wallet))
            }
        fun getBoxStore(): BoxStore {
          return box as BoxStore
        }

    }

    override fun onCreate() {
        super.onCreate()
        initBox()
        isAdmin()
    }

    fun initBox() {
        if (box == null) {
            try {
                box = MyObjectBox.builder().androidContext(this).build()
            } catch (e: DbException) {
                Toast.makeText(this, "Problem initializing DB: " + e.message + "\n Try to remove old data and start from scratch", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun isAdmin() {
        isAdmin = packageName.equals("io.lab10.vallet.admin")
    }
}