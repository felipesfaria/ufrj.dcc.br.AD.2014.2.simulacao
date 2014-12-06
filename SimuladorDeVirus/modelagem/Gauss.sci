function x = Gauss(A,b)
// Substituticao de Gauss - Prof. Dr. José Luiz de Souza Pio
// com pivoteamento parcial.
// entrada:
// --> Gauss(A,b)
// A = matriz de coeficientes
// b = vetor de termos independentes
// saida
//x = vetor solucao
[m,n] = size(A);
if m~=n, error('A Matriz deve ser quadrada'); end
nb = n+1;
Aug = [A b];
// operacoes elementares sobre as linhas
for k = 1:n-1
for i = k+1:n
mult = Aug(i,k)/Aug(k,k);
Aug(i,k:nb) = Aug(i,k:nb)-mult*Aug(k,k:nb);
end
end
// retrosubstituicao
x = zeros(n,1);
x(n) = Aug(n,nb)/Aug(n,n);
for i = n-1:-1:1
x(i) = (Aug(i,nb)-Aug(i,i+1:n)*x(i+1:n))/Aug(i,i);
end
endfunction
