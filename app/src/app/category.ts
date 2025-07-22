export interface Category {
    name: string;
    namePtBr: string
}

export const categoriesEnum: Category[] = [
    {name: 'HEALTH', namePtBr: 'Saúde'},
    {name: 'FOOD', namePtBr: 'Alimentação'},
    {name: 'HOME', namePtBr: 'Casa'},
    {name: 'TRANSPORT', namePtBr: 'Transporte'},
    {name: 'EDUCATION', namePtBr: 'Educação'},
    {name: 'LEISURE', namePtBr: 'Lazer'},
    {name: 'UNFORESEEN', namePtBr: 'Imprevístos'},
    {name: 'OTHERS', namePtBr: 'Outros'}
];

export function getCategoryNameInPortuguese(categoryName: string) {
    let result = categoriesEnum.filter(category => category.name == categoryName);
    return result[0].namePtBr;
}

export function getCategoryNameInEnglish(categoryName: string) {
    let result = categoriesEnum.filter(category => category.namePtBr == categoryName);
    return result[0].name;
}
