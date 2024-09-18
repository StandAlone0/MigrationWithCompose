//
//  ContentView.swift
//  composemigration
//
//  Created by Евгений on 17.09.2024.
//

import SwiftUI
import Shared

struct AppComposable: UIViewControllerRepresentable {
    
    func makeUIViewController(context: Context) -> UIViewController {
        let vc = AppViewControllerKt.AppViewController()
        vc.additionalSafeAreaInsets = .zero
        return vc
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        
    }
}

struct ContentView: View {
    var body: some View {
        AppComposable()
    }
}

#Preview {
    ContentView()
}
