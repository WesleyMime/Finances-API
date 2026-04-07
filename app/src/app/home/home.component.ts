import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HeaderComponent } from "../header/header.component";

interface FeatureCard {
  title: string;
  iconSvg: string;
  description: string;
  open: boolean;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, HeaderComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  features: FeatureCard[] = [
    {
      title: 'Ferramentas de Orçamento',
      iconSvg: "M640-520q17 0 28.5-11.5T680-560q0-17-11.5-28.5T640-600q-17 0-28.5 11.5T600-560q0 17 11.5 28.5T640-520ZM320-620h200v-60H320v60ZM180-120q-34-114-67-227.5T80-580q0-92 64-156t156-64h200q29-38 70.5-59t89.5-21q25 0 42.5 17.5T720-820q0 6-1.5 12t-3.5 11q-4 11-7.5 22.5T702-751l91 91h87v279l-113 37-67 224H480v-80h-80v80H180Zm45-60h115v-80h200v80h115l63-210 102-35v-175h-52L640-728q1-25 6.5-48.5T658-824q-38 10-72 29.5T534-740H300q-66.29 0-113.14 46.86Q140-646.29 140-580q0 103.16 29 201.58Q198-280 225-180Zm255-322Z",
      description: 'Gerencie facilmente seu orçamento mensal, categorize despesas e acompanhe seus gastos em tempo real.',
      open: false
    },
    // {
    //   title: 'Cálculo de Investimentos',
    //   iconSvg: "M120-120v-76l60-60v136h-60Zm165 0v-236l60-60v296h-60Zm165 0v-296l60 61v235h-60Zm165 0v-235l60-60v295h-60Zm165 0v-396l60-60v456h-60ZM120-356v-85l280-278 160 160 280-281v85L560-474 400-634 120-356Z",
    //   description: 'Planeje diferentes cenários de investimento, simule rentabilidade e analise diferentes opções para maximizar seus ganhos.',
    //   open: false
    // },
    {
      title: 'Relatórios Financeiros',
      iconSvg: "M284-277h60v-205h-60v205Zm332 0h60v-420h-60v420Zm-166 0h60v-118h-60v118Zm0-205h60v-60h-60v60ZM180-120q-24 0-42-18t-18-42v-600q0-24 18-42t42-18h600q24 0 42 18t18 42v600q0 24-18 42t-42 18H180Zm0-60h600v-600H180v600Zm0-600v600-600Z",
      description: 'Obtenha relatórios detalhados sobre suas finanças, com gráficos intuitivos e análises para entender melhor sua vida financeira.',
      open: false
    },
    {
      title: 'Análise por IA',
      iconSvg: "M400.06-300Q508-300 584.5-376.13 661-452.25 661-561q0-107.92-76.5-183.46T400.06-820q-107.94 0-183.5 75.54T141-561q0 108.75 75.56 184.87Q292.12-300 400.06-300ZM371-448v-267h60v267h-60Zm-145 0v-184h60v184h-60Zm290 0v-143h60v143h-60ZM838-80 605-314q-42 35-94.24 54.5Q458.51-240 400-240q-133 0-226-93.5T81-560.7q0-133.71 92.8-226.5Q266.59-880 400.3-880q133.7 0 227.2 93T721-561q0 58.51-19.5 110.76Q682-398 647-356l233 233-42 43Z",
      description: 'Aproveite inteligência artificial para identificar padrões nos seus gastos, sugerir economia e prever tendências financeiras.',
      open: false
    },
    {
      title: 'Segurança e Privacidade',
      iconSvg: "M420-360h120l-23-129q20-10 31.5-29t11.5-42q0-33-23.5-56.5T480-640q-33 0-56.5 23.5T400-560q0 23 11.5 42t31.5 29l-23 129Zm60 280q-139-35-229.5-159.5T160-516v-244l320-120 320 120v244q0 152-90.5 276.5T480-80Zm0-84q104-33 172-132t68-220v-189l-240-90-240 90v189q0 121 68 220t172 132Zm0-316Z",
      description: 'Seus dados são protegidos com criptografia avançada, garantindo que suas informações estejam seguras e privadas.',
      open: false
    }
  ];

  toggleFeature(index: number): void {
    this.features[index].open = !this.features[index].open;
  }
}
