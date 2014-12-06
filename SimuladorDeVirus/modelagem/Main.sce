// UFRJ-Instituto de Computação

//Trabalho de Avaliação de Desempenho
// Professores:
//      Daniel Sadoc e Paulo Aguiar
// Alunos:
//      Pedro Braga,
//      Rodrigo Ney,
//      Bruno Kosawa,
//      Felipe Sepulveda de Faria

//Instruções abra o scilab e configure o o diretório como o mesmo que esta esse
// arquivo com chdir, em seguida execute esse codigo no scilab.
getd();
r4 = 1.0;
delta=0.1;
min_r4 = 0.0;
while r4>min_r4
    runSingleNodeModule(r4);
    r4=r4-delta;
end
