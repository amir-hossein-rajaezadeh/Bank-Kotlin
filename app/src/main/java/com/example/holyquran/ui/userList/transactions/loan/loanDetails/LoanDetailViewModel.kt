package com.example.holyquran.ui.userList.transactions.loan.loanDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.holyquran.data.database.LoanDAO
import com.example.holyquran.data.database.TransactionsDAO
import com.example.holyquran.data.database.UserDAO
import com.example.holyquran.data.model.Loan
import com.example.holyquran.data.model.UserInfo

class LoanDetailViewModel(
    val mUserInfoDAO: UserDAO,
    val mTransactionsDAO: TransactionsDAO,
    val mLoan: LoanDAO,
    application: Application
) :
    AndroidViewModel(application) {

    private val _userName = MutableLiveData<UserInfo>()
    val userName: LiveData<UserInfo>
        get() = _userName

    fun setUserName(id: Long): LiveData<UserInfo>? {
        return mUserInfoDAO.get(id)
    }

    fun setUserName(mUser: UserInfo) {
        _userName.value = mUser
    }
    private val _loanDetail = MutableLiveData<Loan>()
    val loanDetail: LiveData<Loan>
        get() = _loanDetail

    fun setLoanDetail(id: Long): LiveData<Loan>? {
        return mLoan.getLoanId(id)
    }

    fun setLoanDetail(mLoanDetail: Loan) {
        _loanDetail.value = mLoanDetail
    }

    }
