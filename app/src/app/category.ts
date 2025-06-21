import { Transaction } from "./add-transaction/transaction";

export interface Category {
    name: string;
    namePtBr: string
}

export const categoriesEnum: Category[] = [
    {name: 'Health', namePtBr: 'Saúde'},
    {name: 'Food', namePtBr: 'Alimentação'},
    {name: 'Home', namePtBr: 'Casa'},
    {name: 'Transport', namePtBr: 'Transporte'},
    {name: 'Education', namePtBr: 'Educação'},
    {name: 'Leisure', namePtBr: 'Lazer'},
    {name: 'Unforeseen', namePtBr: 'Imprevístos'},
    {name: 'Others', namePtBr: 'Outros'}
];

export function getCategoryNameInPortuguese(categoryName: string) {
    debugger
    var result = categoriesEnum.filter(category => category.name == categoryName);
    return result[0].namePtBr;
}

export function getCategoryNameInEnglish(categoryName: string) {
    debugger
    var result = categoriesEnum.filter(category => category.namePtBr == categoryName);
    return result[0].name;
}
