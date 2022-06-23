package com.marlon.trabalhon2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.marlon.trabalhon2.adapter.AdapterProduto
import com.marlon.trabalhon2.databinding.ActivityMainBinding
import com.marlon.trabalhon2.databinding.CustomBottomSheetBinding
import com.marlon.trabalhon2.databinding.ProdutoItemBinding
import com.marlon.trabalhon2.model.Produto

class MainActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        //binding.btnAdicionar.setOnClickListener { showBottomSheetDialog()}

        supportActionBar!!.hide()
        val recyclerView_products = findViewById<RecyclerView>(R.id.recyclerView_products)
        recyclerView_products.layoutManager = LinearLayoutManager(this)
        recyclerView_products.setHasFixedSize(true)

        var listProducts: MutableList<Produto> = mutableListOf()
        var adapterProduto = AdapterProduto(this, listProducts)
        recyclerView_products.adapter = adapterProduto
        adapterProduto.setOnItemClickListener(object : AdapterProduto.onItemClickListener{
            override fun onItemClick(position: Int) {
                //Tentar chamar o treco aqui
                showBottomSheetDialog()
            }

        })

        val product1 = Produto(
            R.drawable.ssd,
            "SSD Sandisk Plus 480GB",
            "Confiável, rápido e muita capacidade. A SanDisk, pioneira em tecnologias de armazenamento de estado sólido é a marca de confiança dos profissionais da área, oferece maior velocidade e desempenho com o SanDisk SSD Plus.",
            "R$ 450,00"
        )
        listProducts.add(product1)

        val product2 = Produto(
            R.drawable.processador,
            "Intel Core i5 10400F",
            "Os novos processadores da 10ª geração oferecem atualizações de desempenho incríveis para melhorar a produtividade e proporcionar entretenimento surpreendente.",
            "R$ 1050,00"
        )
        listProducts.add(product2)

        val product3 = Produto(
            R.drawable.memoria,
            "Memória Ram Corsair  8GB DDR4",
            "Memória Corsair Vengeance LPX, 8GB, 2666MHz, DDR4, C16, Preto.",
            "R$ 369,00"
        )
        listProducts.add(product3)

        val product4 = Produto(
            R.drawable.placadevideo,
            "GeForce RTX 3090 24GB",
            "Os blocos de construção para a GPU mais rápida e eficiente do mundo, o novo Ampere SM traz 2X a taxa de transferência do FP32 e maior eficiência de energia.",
            "R$ 18.499,00"
        )
        listProducts.add(product4)

        val product5 = Produto(
            R.drawable.teclado,
            "Teclado Mecânico Gamer T-Dagger Corvette",
            "Teclado Mecânico Gamer T-Dagger Corvette, LED Rainbow, Switch Outemu DIY Blue, ABNT2 - T-TGK302 -BL (PT-BLUE).",
            "R$ 229,00"
        )
        listProducts.add(product5)

        val product6 = Produto(
            R.drawable.gabinete,
            "Gabinete Gamer",
            "A série Carbide SPEC-DELTA RGB é uma caixa ATX de torre média de vidro temperado com estilo angular impressionante, fluxo de ar potente e três ventiladores de refrigeração RGB incluídos.",
            "R$ 799,00"
        )
        listProducts.add(product6)
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)

        val sheetBinding: CustomBottomSheetBinding =
            CustomBottomSheetBinding.inflate(layoutInflater, null, false)

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }
}